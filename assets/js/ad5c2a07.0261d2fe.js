"use strict";(self.webpackChunkhertzbeat=self.webpackChunkhertzbeat||[]).push([[52884],{15680:(e,t,a)=>{a.d(t,{xA:()=>s,yg:()=>y});var r=a(96540);function n(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function l(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,r)}return a}function i(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?l(Object(a),!0).forEach((function(t){n(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):l(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function o(e,t){if(null==e)return{};var a,r,n=function(e,t){if(null==e)return{};var a,r,n={},l=Object.keys(e);for(r=0;r<l.length;r++)a=l[r],t.indexOf(a)>=0||(n[a]=e[a]);return n}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(r=0;r<l.length;r++)a=l[r],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(n[a]=e[a])}return n}var g=r.createContext({}),c=function(e){var t=r.useContext(g),a=t;return e&&(a="function"==typeof e?e(t):i(i({},t),e)),a},s=function(e){var t=c(e.components);return r.createElement(g.Provider,{value:t},e.children)},m={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},p=r.forwardRef((function(e,t){var a=e.components,n=e.mdxType,l=e.originalType,g=e.parentName,s=o(e,["components","mdxType","originalType","parentName"]),p=c(a),y=n,u=p["".concat(g,".").concat(y)]||p[y]||m[y]||l;return a?r.createElement(u,i(i({ref:t},s),{},{components:a})):r.createElement(u,i({ref:t},s))}));function y(e,t){var a=arguments,n=t&&t.mdxType;if("string"==typeof e||n){var l=a.length,i=new Array(l);i[0]=p;var o={};for(var g in t)hasOwnProperty.call(t,g)&&(o[g]=t[g]);o.originalType=e,o.mdxType="string"==typeof e?e:n,i[1]=o;for(var c=2;c<l;c++)i[c]=a[c];return r.createElement.apply(null,i)}return r.createElement.apply(null,a)}p.displayName="MDXCreateElement"},50963:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>g,contentTitle:()=>i,default:()=>m,frontMatter:()=>l,metadata:()=>o,toc:()=>c});var r=a(58168),n=(a(96540),a(15680));const l={id:"starrocks_fe",title:"Monitoring StarRocks Database FE Monitoring",sidebar_label:"StarRocks Database FE",keywords:["Open Source Monitoring System","Open Source Database Monitoring","StarRocks Database FE Monitoring"]},i=void 0,o={unversionedId:"help/starrocks_fe",id:"help/starrocks_fe",title:"Monitoring StarRocks Database FE Monitoring",description:"Collect and monitor general performance metrics for StarRocks database FE. Supports StarRocks 2.4.0 and later versions.",source:"@site/docs/help/starrocks_fe.md",sourceDirName:"help",slug:"/help/starrocks_fe",permalink:"/docs/help/starrocks_fe",draft:!1,editUrl:"https://github.com/apache/hertzbeat/edit/master/home/docs/help/starrocks_fe.md",tags:[],version:"current",frontMatter:{id:"starrocks_fe",title:"Monitoring StarRocks Database FE Monitoring",sidebar_label:"StarRocks Database FE",keywords:["Open Source Monitoring System","Open Source Database Monitoring","StarRocks Database FE Monitoring"]},sidebar:"docs",previous:{title:"StarRocks Database BE",permalink:"/docs/help/starrocks_be"},next:{title:"ElasticSearch",permalink:"/docs/help/elasticsearch"}},g={},c=[{value:"Pre-monitoring Operations",id:"pre-monitoring-operations",level:3},{value:"Configuration Parameters",id:"configuration-parameters",level:3},{value:"Collection Metrics",id:"collection-metrics",level:3},{value:"Metric Set: jvm_heap_size_bytes",id:"metric-set-jvm_heap_size_bytes",level:4},{value:"Metric Set: jvm_non_heap_size_bytes",id:"metric-set-jvm_non_heap_size_bytes",level:4},{value:"Metric Set: jvm_thread",id:"metric-set-jvm_thread",level:4},{value:"Metric Set: starrocks_fe_query_err",id:"metric-set-starrocks_fe_query_err",level:4},{value:"Metric Set: starrocks_fe_query_latency_ms",id:"metric-set-starrocks_fe_query_latency_ms",level:4},{value:"Metric Set: starrocks_fe_edit_log_write",id:"metric-set-starrocks_fe_edit_log_write",level:4},{value:"Metric Set: starrocks_fe_load_add",id:"metric-set-starrocks_fe_load_add",level:4},{value:"Metric Set: starrocks_fe_load_finished",id:"metric-set-starrocks_fe_load_finished",level:4},{value:"Metric Set: starrocks_fe_job",id:"metric-set-starrocks_fe_job",level:4},{value:"Metric Set: starrocks_fe_tablet_max_compaction_score",id:"metric-set-starrocks_fe_tablet_max_compaction_score",level:4},{value:"Metric Set: starrocks_fe_meta_log_count",id:"metric-set-starrocks_fe_meta_log_count",level:4},{value:"Metric Set: starrocks_fe_query_total",id:"metric-set-starrocks_fe_query_total",level:4},{value:"Metric Set: starrocks_fe_request_total",id:"metric-set-starrocks_fe_request_total",level:4},{value:"Metric Set: starrocks_fe_txn_reject",id:"metric-set-starrocks_fe_txn_reject",level:4},{value:"Metric Set: starrocks_fe_txn_begin",id:"metric-set-starrocks_fe_txn_begin",level:4},{value:"Metric Set: starrocks_fe_txn_success",id:"metric-set-starrocks_fe_txn_success",level:4},{value:"Metric Set: starrocks_fe_txn_failed",id:"metric-set-starrocks_fe_txn_failed",level:4},{value:"Metric Set: starrocks_fe_connection_total",id:"metric-set-starrocks_fe_connection_total",level:4}],s={toc:c};function m(e){let{components:t,...a}=e;return(0,n.yg)("wrapper",(0,r.A)({},s,a,{components:t,mdxType:"MDXLayout"}),(0,n.yg)("blockquote",null,(0,n.yg)("p",{parentName:"blockquote"},"Collect and monitor general performance metrics for StarRocks database FE. Supports StarRocks 2.4.0 and later versions.")),(0,n.yg)("p",null,(0,n.yg)("strong",{parentName:"p"},"Protocol: HTTP")),(0,n.yg)("h3",{id:"pre-monitoring-operations"},"Pre-monitoring Operations"),(0,n.yg)("p",null,"Check the ",(0,n.yg)("inlineCode",{parentName:"p"},"fe/conf/fe.conf")," file to obtain the value of the ",(0,n.yg)("inlineCode",{parentName:"p"},"http_port")," configuration item, which is used for monitoring."),(0,n.yg)("h3",{id:"configuration-parameters"},"Configuration Parameters"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Parameter Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Parameter Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"Monitor Host"),(0,n.yg)("td",{parentName:"tr",align:null},"The monitored target's IPV4, IPV6, or domain name. Note: Without the protocol header (e.g., https://, http://)")),(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"Task Name"),(0,n.yg)("td",{parentName:"tr",align:null},"A unique name identifying this monitoring task")),(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"Port"),(0,n.yg)("td",{parentName:"tr",align:null},"The port provided by the database to the outside, default is 8030, get the value of the ",(0,n.yg)("inlineCode",{parentName:"td"},"http_port")," configuration item")),(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"Query Timeout"),(0,n.yg)("td",{parentName:"tr",align:null},"The timeout for the connection to not respond, in milliseconds, default is 6000 milliseconds")),(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"Description"),(0,n.yg)("td",{parentName:"tr",align:null},"Additional notes and descriptions for this monitoring task")))),(0,n.yg)("h3",{id:"collection-metrics"},"Collection Metrics"),(0,n.yg)("blockquote",null,(0,n.yg)("p",{parentName:"blockquote"},"For more metrics, please refer to the StarRocks official documentation: ",(0,n.yg)("a",{parentName:"p",href:"https://docs.mirrorship.cn/docs/administration/management/monitoring/metrics/"},"General Monitoring Metrics"),".")),(0,n.yg)("h4",{id:"metric-set-jvm_heap_size_bytes"},"Metric Set: jvm_heap_size_bytes"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"Byte"),(0,n.yg)("td",{parentName:"tr",align:null},"JVM heap memory size")))),(0,n.yg)("h4",{id:"metric-set-jvm_non_heap_size_bytes"},"Metric Set: jvm_non_heap_size_bytes"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"Byte"),(0,n.yg)("td",{parentName:"tr",align:null},"JVM non-heap memory size")))),(0,n.yg)("h4",{id:"metric-set-jvm_thread"},"Metric Set: jvm_thread"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of JVM threads")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_query_err"},"Metric Set: starrocks_fe_query_err"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of failed queries")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_query_latency_ms"},"Metric Set: starrocks_fe_query_latency_ms"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"ms"),(0,n.yg)("td",{parentName:"tr",align:null},"Query response time")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_edit_log_write"},"Metric Set: starrocks_fe_edit_log_write"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"Byte/s"),(0,n.yg)("td",{parentName:"tr",align:null},"Write speed of FE edit log")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_load_add"},"Metric Set: starrocks_fe_load_add"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of new load jobs")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_load_finished"},"Metric Set: starrocks_fe_load_finished"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of finished load jobs")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_job"},"Metric Set: starrocks_fe_job"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"FE job status")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_tablet_max_compaction_score"},"Metric Set: starrocks_fe_tablet_max_compaction_score"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Indicates the highest Compaction Score on each BE node")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_meta_log_count"},"Metric Set: starrocks_fe_meta_log_count"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"The number of Edit Logs without a checkpoint. A value within 100000 is reasonable")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_query_total"},"Metric Set: starrocks_fe_query_total"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Total number of queries")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_request_total"},"Metric Set: starrocks_fe_request_total"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Total number of requests")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_txn_reject"},"Metric Set: starrocks_fe_txn_reject"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of rejected transactions")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_txn_begin"},"Metric Set: starrocks_fe_txn_begin"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of beginning transactions")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_txn_success"},"Metric Set: starrocks_fe_txn_success"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of successful transactions")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_txn_failed"},"Metric Set: starrocks_fe_txn_failed"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Number of failed transactions")))),(0,n.yg)("h4",{id:"metric-set-starrocks_fe_connection_total"},"Metric Set: starrocks_fe_connection_total"),(0,n.yg)("table",null,(0,n.yg)("thead",{parentName:"table"},(0,n.yg)("tr",{parentName:"thead"},(0,n.yg)("th",{parentName:"tr",align:null},"Metric Name"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Unit"),(0,n.yg)("th",{parentName:"tr",align:null},"Metric Help Description"))),(0,n.yg)("tbody",{parentName:"table"},(0,n.yg)("tr",{parentName:"tbody"},(0,n.yg)("td",{parentName:"tr",align:null},"value"),(0,n.yg)("td",{parentName:"tr",align:null},"None"),(0,n.yg)("td",{parentName:"tr",align:null},"Total number of FE connections")))))}m.isMDXComponent=!0}}]);