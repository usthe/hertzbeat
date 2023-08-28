package org.dromara.hertzbeat.manager.netty.process;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hertzbeat.common.entity.message.ClusterMsg;
import org.dromara.hertzbeat.common.entity.message.CollectRep;
import org.dromara.hertzbeat.common.util.JsonUtil;
import org.dromara.hertzbeat.common.util.ProtoJsonUtil;
import org.dromara.hertzbeat.manager.netty.ManageServer;
import org.dromara.hertzbeat.remoting.netty.NettyRemotingProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * handle one-time collect data message
 */
@Slf4j
public class CollectOneTimeDataProcessor implements NettyRemotingProcessor {

    private final ManageServer manageServer;

    public CollectOneTimeDataProcessor(ManageServer manageServer) {
        this.manageServer = manageServer;
    }

    @Override
    public ClusterMsg.Message handle(ClusterMsg.Message message) {
        TypeReference<List<String>> typeReference = new TypeReference<>() {
        };
        List<String> jsonArr = JsonUtil.fromJson(message.getMsg(), typeReference);
        if (jsonArr == null) {
            log.error("netty receive response one time task data parse null error");
            return null;
        }
        List<CollectRep.MetricsData> metricsDataList = new ArrayList<>(jsonArr.size());
        for (String str : jsonArr) {
            CollectRep.MetricsData metricsData = (CollectRep.MetricsData) ProtoJsonUtil.toProtobuf(str,
                    CollectRep.MetricsData.newBuilder());
            if (metricsData != null) {
                metricsDataList.add(metricsData);
            }
        }
        this.manageServer.getCollectJobScheduling().collectSyncJobResponse(metricsDataList);
        return null;
    }
}
