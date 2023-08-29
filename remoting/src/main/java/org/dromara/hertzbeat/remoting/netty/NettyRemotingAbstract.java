package org.dromara.hertzbeat.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.epoll.Epoll;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hertzbeat.common.entity.message.ClusterMsg;
import org.dromara.hertzbeat.common.util.NetworkUtil;
import org.dromara.hertzbeat.remoting.RemotingService;
import org.dromara.hertzbeat.remoting.event.NettyEvent;
import org.dromara.hertzbeat.remoting.event.NettyEventExecutor;
import org.dromara.hertzbeat.remoting.event.NettyEventListener;

import java.util.concurrent.ConcurrentHashMap;

/**
 * netty remote abstract
 */
@Slf4j
public abstract class NettyRemotingAbstract implements RemotingService {
    protected ConcurrentHashMap<ClusterMsg.MessageType, NettyRemotingProcessor> processorTable = new ConcurrentHashMap<>();

    protected ConcurrentHashMap<String, ResponseFuture> responseTable = new ConcurrentHashMap<>();

    protected NettyEventExecutor nettyEventExecutor;

    protected NettyRemotingAbstract(NettyEventListener nettyEventListener) {
        if (nettyEventListener != null) {
            this.nettyEventExecutor = new NettyEventExecutor(nettyEventListener);
        }
    }

    public void registerProcessor(final ClusterMsg.MessageType messageType, final NettyRemotingProcessor processor) {
        this.processorTable.put(messageType, processor);
    }

    protected void processReceiveMsg(ChannelHandlerContext ctx, ClusterMsg.Message message) {
        NettyRemotingProcessor processor = this.processorTable.get(message.getType());
        if (processor == null) {

            log.info("request type {} not supported", message.getType());
            return;
        }
        ClusterMsg.Message response = processor.handle(message);
        if (response != null) {
            if (this.responseTable.containsKey(message.getIdentity())) {
                ResponseFuture responseFuture = this.responseTable.get(message.getIdentity());
                responseFuture.putResponse(response);
            }
            ctx.writeAndFlush(response);
        }
    }

    protected ClusterMsg.Message sendMsgSync(final Channel channel, final ClusterMsg.Message request, final int timeoutMillis) {
        final String identity = request.getIdentity();

        try {
            ResponseFuture responseFuture = new ResponseFuture();
            this.responseTable.put(identity, responseFuture);
            channel.writeAndFlush(request).addListener(future -> {
                if (!future.isSuccess()) {
                    responseTable.remove(identity);
                    log.warn("failed to send request message to server. address: {}, ", channel.remoteAddress(), future.cause());
                }
            });
            ClusterMsg.Message response = responseFuture.waitResponse(timeoutMillis);
            if (response == null) {
                log.warn("get response message from server is null");
            }
            return response;
        } catch (InterruptedException e) {
            log.warn("failed to get response message from server, ", e);
        } finally {
            responseTable.remove(identity);
        }
        return null;
    }

    protected void addEvent(Channel channel) {
        if (this.nettyEventExecutor == null) {
            return;
        }
        NettyEvent nettyEvent = new NettyEvent(channel);
        this.nettyEventExecutor.addEvent(nettyEvent);
    }

    protected boolean useEpoll() {
        return NetworkUtil.isLinuxPlatform()
                && Epoll.isAvailable();
    }

}
