"use strict";(self.webpackChunkhertzbeat=self.webpackChunkhertzbeat||[]).push([[68321],{15680:(e,t,a)=>{a.d(t,{xA:()=>i,yg:()=>s});var n=a(96540);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function l(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function g(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?l(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):l(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function p(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},l=Object.keys(e);for(n=0;n<l.length;n++)a=l[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(n=0;n<l.length;n++)a=l[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var m=n.createContext({}),o=function(e){var t=n.useContext(m),a=t;return e&&(a="function"==typeof e?e(t):g(g({},t),e)),a},i=function(e){var t=o(e.components);return n.createElement(m.Provider,{value:t},e.children)},y={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},u=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,l=e.originalType,m=e.parentName,i=p(e,["components","mdxType","originalType","parentName"]),u=o(a),s=r,d=u["".concat(m,".").concat(s)]||u[s]||y[s]||l;return a?n.createElement(d,g(g({ref:t},i),{},{components:a})):n.createElement(d,g({ref:t},i))}));function s(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var l=a.length,g=new Array(l);g[0]=u;var p={};for(var m in t)hasOwnProperty.call(t,m)&&(p[m]=t[m]);p.originalType=e,p.mdxType="string"==typeof e?e:r,g[1]=p;for(var o=2;o<l;o++)g[o]=a[o];return n.createElement.apply(null,g)}return n.createElement.apply(null,a)}u.displayName="MDXCreateElement"},661:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>m,contentTitle:()=>g,default:()=>y,frontMatter:()=>l,metadata:()=>p,toc:()=>o});var n=a(58168),r=(a(96540),a(15680));const l={id:"spark",title:"\u76d1\u63a7 Spark",sidebar_label:"Spark \u76d1\u63a7",keywords:["\u5f00\u6e90\u76d1\u63a7\u5de5\u5177\uff0c\u5f00\u6e90Java spark\u76d1\u63a7\u5de5\u5177\uff0c\u76d1\u63a7spark\u6307\u6807"]},g=void 0,p={unversionedId:"help/spark",id:"help/spark",title:"\u76d1\u63a7 Spark",description:"\u6536\u96c6\u548c\u76d1\u63a7Spark\u7684\u4e00\u822c\u6027\u80fd\u6307\u6807\u3002",source:"@site/i18n/zh-cn/docusaurus-plugin-content-docs/current/help/spark.md",sourceDirName:"help",slug:"/help/spark",permalink:"/zh-cn/docs/help/spark",draft:!1,editUrl:"https://github.com/apache/hertzbeat/edit/master/home/i18n/zh-cn/docusaurus-plugin-content-docs/current/help/spark.md",tags:[],version:"current",frontMatter:{id:"spark",title:"\u76d1\u63a7 Spark",sidebar_label:"Spark \u76d1\u63a7",keywords:["\u5f00\u6e90\u76d1\u63a7\u5de5\u5177\uff0c\u5f00\u6e90Java spark\u76d1\u63a7\u5de5\u5177\uff0c\u76d1\u63a7spark\u6307\u6807"]},sidebar:"docs",previous:{title:"SeaTunnel",permalink:"/zh-cn/docs/help/seatunnel"},next:{title:"Apache Yarn",permalink:"/zh-cn/docs/help/yarn"}},m={},o=[{value:"Spark App\u542f\u7528JMX\u534f\u8bae\u6b65\u9aa4",id:"spark-app\u542f\u7528jmx\u534f\u8bae\u6b65\u9aa4",level:3},{value:"\u7b2c\u4e00\u6b65",id:"\u7b2c\u4e00\u6b65",level:2},{value:"\u7b2c\u4e8c\u6b65",id:"\u7b2c\u4e8c\u6b65",level:2},{value:"\u7b2c\u4e09\u6b65",id:"\u7b2c\u4e09\u6b65",level:2},{value:"\u914d\u7f6e\u53c2\u6570",id:"\u914d\u7f6e\u53c2\u6570",level:3},{value:"\u91c7\u96c6\u6307\u6807",id:"\u91c7\u96c6\u6307\u6807",level:3},{value:"\u6307\u6807\u96c6\u5408\uff1amemory_pool",id:"\u6307\u6807\u96c6\u5408memory_pool",level:4},{value:"Metrics Set\uff1acode_cache (\u4ec5\u652f\u6301 JDK8)",id:"metrics-setcode_cache-\u4ec5\u652f\u6301-jdk8",level:4},{value:"\u6307\u6807\u96c6\u5408\uff1aclass_loading",id:"\u6307\u6807\u96c6\u5408class_loading",level:4},{value:"\u6307\u6807\u96c6\u5408\uff1athread",id:"\u6307\u6807\u96c6\u5408thread",level:4}],i={toc:o};function y(e){let{components:t,...a}=e;return(0,r.yg)("wrapper",(0,n.A)({},i,a,{components:t,mdxType:"MDXLayout"}),(0,r.yg)("blockquote",null,(0,r.yg)("p",{parentName:"blockquote"},"\u6536\u96c6\u548c\u76d1\u63a7Spark\u7684\u4e00\u822c\u6027\u80fd\u6307\u6807\u3002")),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"\u4f7f\u7528\u534f\u8bae\uff1aJMX")),(0,r.yg)("h3",{id:"spark-app\u542f\u7528jmx\u534f\u8bae\u6b65\u9aa4"},"Spark App\u542f\u7528JMX\u534f\u8bae\u6b65\u9aa4"),(0,r.yg)("ol",null,(0,r.yg)("li",{parentName:"ol"},"\u5e94\u7528\u542f\u52a8\u65f6\u6dfb\u52a0Spark\u53c2\u6570 \u26a0\ufe0f\u6ce8\u610f\u53ef\u81ea\u5b9a\u4e49\u66b4\u9732\u7aef\u53e3,\u5bf9\u5916IP")),(0,r.yg)("p",null,"\u53c2\u8003\u6587\u6863: ",(0,r.yg)("a",{parentName:"p",href:"https://spark.apache.org/docs/latest/spark-standalone.html"},"https://spark.apache.org/docs/latest/spark-standalone.html")),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"\u76d1\u63a7\u914d\u7f6espark\u7684\u76d1\u63a7\u4e3b\u8981\u5206\u4e3aMaster\u3001Worker\u3001driver\u3001executor\u76d1\u63a7\u3002Master\u548cWorker\u7684\u76d1\u63a7\u5728spark\u96c6\u7fa4\u8fd0\u884c\u65f6\u5373\u53ef\u76d1\u63a7\uff0cDriver\u548cExcutor\u7684\u76d1\u63a7\u9700\u8981\u9488\u5bf9\u67d0\u4e00\u4e2aapp\u6765\u8fdb\u884c\u76d1\u63a7\u3002"),"\n",(0,r.yg)("strong",{parentName:"p"},"\u5982\u679c\u90fd\u8981\u76d1\u63a7\uff0c\u9700\u8981\u6839\u636e\u4ee5\u4e0b\u6b65\u9aa4\u6765\u914d\u7f6e")),(0,r.yg)("h2",{id:"\u7b2c\u4e00\u6b65"},"\u7b2c\u4e00\u6b65"),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"\u4fee\u6539$SPARK_HOME/conf/spark-env.sh\uff0c\u6dfb\u52a0\u4ee5\u4e0b\u8bed\u53e5\uff1a")),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-shell"},'# JMX Port to use\nSPARK_DAEMON_JAVA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \n\n# export SPARK_DAEMON_JAVA_OPTS="$SPARK_DAEMON_JAVA_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT "\nexport SPARK_DAEMON_JAVA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=8712 "\n')),(0,r.yg)("p",null,"\u8bed\u53e5\u4e2d\u6709$JMX_PORT\uff0c\u8fd9\u4e2a\u7684\u503c\u53ef\u4ee5\u81ea\u5b9a\u4e49\uff0c\u4e5f\u53ef\u4ee5\u83b7\u53d6\u4e00\u4e2a\u968f\u673a\u6570\u4f5c\u4e3a\u7aef\u53e3\u53f7\u3002\n\u5982\u679c\u7aef\u53e3\u81ea\u5b9a\u4e49\u4e3a\u4e00\u4e2a\u5177\u4f53\u7684\u503c\uff0c\u800c spark \u7684 Master \u548c\u5176\u4e2d\u4e4b\u4e00\u7684 Worker \u5728\u540c\u4e00\u53f0\u673a\u5668\u4e0a\uff0c\u4f1a\u51fa\u73b0\u7aef\u53e3\u51b2\u7a81\u7684\u60c5\u51b5\u3002"),(0,r.yg)("h2",{id:"\u7b2c\u4e8c\u6b65"},"\u7b2c\u4e8c\u6b65"),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"vim $SPARK_HOME/conf/metrics.properties \u6dfb\u52a0\u5982\u4e0b\u5185\u5bb9")),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-shell"},"*.sink.jmx.class=org.apache.spark.metrics.sink.JmxSink\nmaster.source.jvm.class=org.apache.spark.metrics.source.JvmSource\nworker.source.jvm.class=org.apache.spark.metrics.source.JvmSource\ndriver.source.jvm.class=org.apache.spark.metrics.source.JvmSource\nexecutor.source.jvm.class=org.apache.spark.metrics.source.JvmSource\n")),(0,r.yg)("h2",{id:"\u7b2c\u4e09\u6b65"},"\u7b2c\u4e09\u6b65"),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"vim $SPARK_HOME/conf/spark-defaults.conf\uff0c\u6dfb\u52a0\u4ee5\u4e0b\u9879\u4e3adriver\u548cexecutor\u8bbe\u7f6e\u76d1\u63a7\u7aef\u53e3\uff0c\u5728\u6709\u7a0b\u5e8f\u8fd0\u884c\u7684\u60c5\u51b5\u4e0b\uff0c\u6b64\u7aef\u53e3\u4f1a\u88ab\u6253\u5f00\u3002")),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-shell"},"spark.metrics.conf /opt/bigdata/spark/conf/metrics.properties\nspark.driver.extraJavaOptions -XX:+PrintGCDetails -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.mana\ngement.jmxremote.port=8712\n\nspark.executor.extraJavaOptions -XX:+PrintGCDetails -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.mana\ngement.jmxremote.port=8711\n")),(0,r.yg)("p",null,"\u5728spark\u7684Master\u548cWorker\u6b63\u5e38\u8fd0\u884c\u4ee5\u53caspark-submit\u63d0\u4ea4\u4e86\u4e00\u4e2a\u7a0b\u5e8f\u7684\u60c5\u51b5\u4e0b\uff0c\u53ef\u4ee5\u4ecelinux\u4e2d\u67e5\u8be2\u51fa\u7aef\u53e3\u53f7\u7801\u3002"),(0,r.yg)("h3",{id:"\u914d\u7f6e\u53c2\u6570"},"\u914d\u7f6e\u53c2\u6570"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u53c2\u6570\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u53c2\u6570\u5e2e\u52a9\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Monitoring Host"),(0,r.yg)("td",{parentName:"tr",align:null},"\u88ab\u76d1\u63a7\u7684\u5bf9\u7aefIPV4\uff0cIPV6\u6216\u57df\u540d\u3002\u6ce8\u610f\u26a0\ufe0f\u4e0d\u5e26\u534f\u8bae\u5934(eg: https://, http://)\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Monitoring name"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6807\u8bc6\u6b64\u76d1\u63a7\u7684\u540d\u79f0\u3002\u540d\u79f0\u5fc5\u987b\u662f\u552f\u4e00\u7684")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Port"),(0,r.yg)("td",{parentName:"tr",align:null},"JMX\u63d0\u4f9b\u7684\u7aef\u53e3")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Username"),(0,r.yg)("td",{parentName:"tr",align:null},"JMX\u8fde\u63a5\u7528\u6237\u540d\uff0c\u53ef\u9009")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Password"),(0,r.yg)("td",{parentName:"tr",align:null},"JMX\u8fde\u63a5\u5bc6\u7801\uff0c\u53ef\u9009")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Collection interval"),(0,r.yg)("td",{parentName:"tr",align:null},"\u76d1\u63a7\u5468\u671f\u6027\u91c7\u96c6\u6570\u636e\u95f4\u9694\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2\uff0c\u53ef\u8bbe\u7f6e\u7684\u6700\u5c0f\u95f4\u9694\u4e3a30\u79d2")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Whether to detect"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6dfb\u52a0\u76d1\u63a7\u524d\u662f\u5426\u68c0\u6d4b\u548c\u68c0\u67e5\u76d1\u63a7\u7684\u53ef\u7528\u6027\u3002\u68c0\u6d4b\u6210\u529f\u540e\uff0c\u6dfb\u52a0\u548c\u4fee\u6539\u64cd\u4f5c\u624d\u4f1a\u7ee7\u7eed\u8fdb\u884c")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"Description remarks"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6709\u5173\u8bc6\u522b\u548c\u63cf\u8ff0\u6b64\u76d1\u89c6\u7684\u66f4\u591a\u4fe1\u606f\uff0c\u7528\u6237\u53ef\u4ee5\u5728\u8fd9\u91cc\u8bb0\u5f55\u4fe1\u606f")))),(0,r.yg)("h3",{id:"\u91c7\u96c6\u6307\u6807"},"\u91c7\u96c6\u6307\u6807"),(0,r.yg)("h4",{id:"\u6307\u6807\u96c6\u5408memory_pool"},"\u6307\u6807\u96c6\u5408\uff1amemory_pool"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5355\u4f4d"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"name"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"committed"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u603b\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"init"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u521d\u59cb\u5316\u5927\u5c0f")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"max"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6700\u5927\u503c")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"used"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u5df2\u4f7f\u7528")))),(0,r.yg)("h4",{id:"metrics-setcode_cache-\u4ec5\u652f\u6301-jdk8"},"Metrics Set\uff1acode_cache (\u4ec5\u652f\u6301 JDK8)"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5355\u4f4d"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"committed"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u603b\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"init"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u521d\u59cb\u5316\u5927\u5c0f")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"max"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6700\u5927\u503c")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"used"),(0,r.yg)("td",{parentName:"tr",align:null},"kb"),(0,r.yg)("td",{parentName:"tr",align:null},"\u5df2\u4f7f\u7528")))),(0,r.yg)("h4",{id:"\u6307\u6807\u96c6\u5408class_loading"},"\u6307\u6807\u96c6\u5408\uff1aclass_loading"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5355\u4f4d"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"LoadedClassCount"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u5df2\u52a0\u8f7d\u7c7b\u6570\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"TotalLoadedClassCount"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u5386\u53f2\u5df2\u52a0\u8f7d\u7c7b\u603b\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"UnloadedClassCount"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u672a\u52a0\u8f7d\u7c7b\u6570\u91cf")))),(0,r.yg)("h4",{id:"\u6307\u6807\u96c6\u5408thread"},"\u6307\u6807\u96c6\u5408\uff1athread"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5355\u4f4d"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"TotalStartedThreadCount"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u5df2\u7ecf\u5f00\u59cb\u7684\u7ebf\u7a0b\u6570\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"ThreadCount"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u7ebf\u7a0b\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"PeakThreadCount"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u672a\u52a0\u8f7d\u7c7b\u6570\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"DaemonThreadCount"),(0,r.yg)("td",{parentName:"tr",align:null}),(0,r.yg)("td",{parentName:"tr",align:null},"\u5b88\u62a4\u8fdb\u7a0b\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"CurrentThreadUserTime"),(0,r.yg)("td",{parentName:"tr",align:null},"ms"),(0,r.yg)("td",{parentName:"tr",align:null},"\u4f7f\u7528\u65f6\u95f4")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"CurrentThreadCpuTime"),(0,r.yg)("td",{parentName:"tr",align:null},"ms"),(0,r.yg)("td",{parentName:"tr",align:null},"\u4f7f\u7528CPU\u65f6\u95f4")))))}y.isMDXComponent=!0}}]);