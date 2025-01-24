"use strict";(self.webpackChunkhertzbeat=self.webpackChunkhertzbeat||[]).push([[98433],{15680:(t,e,a)=>{a.d(e,{xA:()=>d,yg:()=>o});var n=a(96540);function r(t,e,a){return e in t?Object.defineProperty(t,e,{value:a,enumerable:!0,configurable:!0,writable:!0}):t[e]=a,t}function l(t,e){var a=Object.keys(t);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(t);e&&(n=n.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),a.push.apply(a,n)}return a}function g(t){for(var e=1;e<arguments.length;e++){var a=null!=arguments[e]?arguments[e]:{};e%2?l(Object(a),!0).forEach((function(e){r(t,e,a[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(a)):l(Object(a)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(a,e))}))}return t}function y(t,e){if(null==t)return{};var a,n,r=function(t,e){if(null==t)return{};var a,n,r={},l=Object.keys(t);for(n=0;n<l.length;n++)a=l[n],e.indexOf(a)>=0||(r[a]=t[a]);return r}(t,e);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(t);for(n=0;n<l.length;n++)a=l[n],e.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(t,a)&&(r[a]=t[a])}return r}var i=n.createContext({}),p=function(t){var e=n.useContext(i),a=e;return t&&(a="function"==typeof t?t(e):g(g({},e),t)),a},d=function(t){var e=p(t.components);return n.createElement(i.Provider,{value:e},t.children)},m={inlineCode:"code",wrapper:function(t){var e=t.children;return n.createElement(n.Fragment,{},e)}},u=n.forwardRef((function(t,e){var a=t.components,r=t.mdxType,l=t.originalType,i=t.parentName,d=y(t,["components","mdxType","originalType","parentName"]),u=p(a),o=r,N=u["".concat(i,".").concat(o)]||u[o]||m[o]||l;return a?n.createElement(N,g(g({ref:e},d),{},{components:a})):n.createElement(N,g({ref:e},d))}));function o(t,e){var a=arguments,r=e&&e.mdxType;if("string"==typeof t||r){var l=a.length,g=new Array(l);g[0]=u;var y={};for(var i in e)hasOwnProperty.call(e,i)&&(y[i]=e[i]);y.originalType=t,y.mdxType="string"==typeof t?t:r,g[1]=y;for(var p=2;p<l;p++)g[p]=a[p];return n.createElement.apply(null,g)}return n.createElement.apply(null,a)}u.displayName="MDXCreateElement"},15275:(t,e,a)=>{a.r(e),a.d(e,{assets:()=>i,contentTitle:()=>g,default:()=>m,frontMatter:()=>l,metadata:()=>y,toc:()=>p});var n=a(58168),r=(a(96540),a(15680));const l={id:"elasticsearch",title:"\u76d1\u63a7\uff1aElasticSearch",sidebar_label:"ElasticSearch",keywords:["\u5f00\u6e90\u76d1\u63a7\u7cfb\u7edf","\u76d1\u63a7ElasticSearch"]},g=void 0,y={unversionedId:"help/elasticsearch",id:"help/elasticsearch",title:"\u76d1\u63a7\uff1aElasticSearch",description:"\u5bf9ElasticSearch\u7684\u901a\u7528\u6027\u80fd\u6307\u6807\u8fdb\u884c\u91c7\u96c6\u76d1\u63a7\u3002",source:"@site/i18n/zh-cn/docusaurus-plugin-content-docs/current/help/elasticsearch.md",sourceDirName:"help",slug:"/help/elasticsearch",permalink:"/zh-cn/docs/help/elasticsearch",draft:!1,editUrl:"https://github.com/apache/hertzbeat/edit/master/home/i18n/zh-cn/docusaurus-plugin-content-docs/current/help/elasticsearch.md",tags:[],version:"current",frontMatter:{id:"elasticsearch",title:"\u76d1\u63a7\uff1aElasticSearch",sidebar_label:"ElasticSearch",keywords:["\u5f00\u6e90\u76d1\u63a7\u7cfb\u7edf","\u76d1\u63a7ElasticSearch"]},sidebar:"docs",previous:{title:"StarRocks\u6570\u636e\u5e93FE",permalink:"/zh-cn/docs/help/starrocks_fe"},next:{title:"Flink \u76d1\u63a7",permalink:"/zh-cn/docs/help/flink"}},i={},p=[{value:"\u914d\u7f6e\u53c2\u6570",id:"\u914d\u7f6e\u53c2\u6570",level:3},{value:"\u91c7\u96c6\u6307\u6807",id:"\u91c7\u96c6\u6307\u6807",level:3},{value:"\u6307\u6807\u96c6\u5408\uff1ahealth",id:"\u6307\u6807\u96c6\u5408health",level:4},{value:"\u6307\u6807\u96c6\u5408\uff1anodes",id:"\u6307\u6807\u96c6\u5408nodes",level:4},{value:"\u6307\u6807\u96c6\u5408\uff1anodes_detail",id:"\u6307\u6807\u96c6\u5408nodes_detail",level:4}],d={toc:p};function m(t){let{components:e,...a}=t;return(0,r.yg)("wrapper",(0,n.A)({},d,a,{components:e,mdxType:"MDXLayout"}),(0,r.yg)("blockquote",null,(0,r.yg)("p",{parentName:"blockquote"},"\u5bf9ElasticSearch\u7684\u901a\u7528\u6027\u80fd\u6307\u6807\u8fdb\u884c\u91c7\u96c6\u76d1\u63a7\u3002")),(0,r.yg)("h3",{id:"\u914d\u7f6e\u53c2\u6570"},"\u914d\u7f6e\u53c2\u6570"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u53c2\u6570\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u53c2\u6570\u5e2e\u52a9\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u76ee\u6807Host"),(0,r.yg)("td",{parentName:"tr",align:null},"\u88ab\u76d1\u63a7\u7684\u5bf9\u7aefIPV4\uff0cIPV6\u6216\u57df\u540d\u3002\u6ce8\u610f\u26a0\ufe0f\u4e0d\u5e26\u534f\u8bae\u5934(eg: https://, http://)\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u4efb\u52a1\u540d\u79f0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6807\u8bc6\u6b64\u76d1\u63a7\u7684\u540d\u79f0\uff0c\u540d\u79f0\u9700\u8981\u4fdd\u8bc1\u552f\u4e00\u6027\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u7aef\u53e3"),(0,r.yg)("td",{parentName:"tr",align:null},"ElasticSearch\u5f00\u653e\u7684HTTP API\u7aef\u53e3\uff0c\u9ed8\u8ba4\u503c\uff1a9200\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u542f\u7528HTTPS"),(0,r.yg)("td",{parentName:"tr",align:null},"\u662f\u5426\u542f\u7528HTTPS\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u8ba4\u8bc1\u65b9\u5f0f"),(0,r.yg)("td",{parentName:"tr",align:null},"\u8fde\u63a5ElasticSearch\u4f7f\u7528\u7684\u8ba4\u8bc1\u65b9\u5f0f\uff0c\u53ef\u9009\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u7528\u6237\u540d"),(0,r.yg)("td",{parentName:"tr",align:null},"\u8fde\u63a5ElasticSearch\u7684\u7528\u6237\u540d\uff0c\u53ef\u9009\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u5bc6\u7801"),(0,r.yg)("td",{parentName:"tr",align:null},"\u8fde\u63a5ElasticSearch\u7684\u5bc6\u7801\uff0c\u53ef\u9009")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u67e5\u8be2\u8d85\u65f6\u65f6\u95f4"),(0,r.yg)("td",{parentName:"tr",align:null},"\u8bbe\u7f6e\u67e5\u8be2\u672a\u54cd\u5e94\u6570\u636e\u65f6\u7684\u8d85\u65f6\u65f6\u95f4\uff0c\u5355\u4f4dms\u6beb\u79d2\uff0c\u9ed8\u8ba46000\u6beb\u79d2\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u91c7\u96c6\u95f4\u9694"),(0,r.yg)("td",{parentName:"tr",align:null},"\u76d1\u63a7\u5468\u671f\u6027\u91c7\u96c6\u6570\u636e\u95f4\u9694\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2\uff0c\u53ef\u8bbe\u7f6e\u7684\u6700\u5c0f\u95f4\u9694\u4e3a30\u79d2")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u7ed1\u5b9a\u6807\u7b7e"),(0,r.yg)("td",{parentName:"tr",align:null},"\u7528\u4e8e\u5bf9\u76d1\u63a7\u8d44\u6e90\u8fdb\u884c\u5206\u7c7b\u7ba1\u7406\u3002")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"\u63cf\u8ff0\u5907\u6ce8"),(0,r.yg)("td",{parentName:"tr",align:null},"\u66f4\u591a\u6807\u8bc6\u548c\u63cf\u8ff0\u6b64\u76d1\u63a7\u7684\u5907\u6ce8\u4fe1\u606f\uff0c\u7528\u6237\u53ef\u4ee5\u5728\u8fd9\u91cc\u5907\u6ce8\u4fe1\u606f\u3002")))),(0,r.yg)("h3",{id:"\u91c7\u96c6\u6307\u6807"},"\u91c7\u96c6\u6307\u6807"),(0,r.yg)("h4",{id:"\u6307\u6807\u96c6\u5408health"},"\u6307\u6807\u96c6\u5408\uff1ahealth"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5355\u4f4d"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5e2e\u52a9\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"cluster_name"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u96c6\u7fa4\u540d\u79f0")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"status"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u96c6\u7fa4\u72b6\u6001")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"nodes"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u96c6\u7fa4\u8282\u70b9\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"data_nodes"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6570\u636e\u8282\u70b9\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"active_primary_shards"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u4e3b\u8282\u70b9\u6d3b\u8dc3\u5206\u7247\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"active_shards"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u6d3b\u8dc3\u5206\u7247\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"active_percentage"),(0,r.yg)("td",{parentName:"tr",align:null},"%"),(0,r.yg)("td",{parentName:"tr",align:null},"\u5206\u7247\u5065\u5eb7\u5ea6")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"initializing_shards"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u521d\u59cb\u5316\u5206\u7247\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"unassigned_shards"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u672a\u5206\u914d\u5206\u7247\u6570")))),(0,r.yg)("h4",{id:"\u6307\u6807\u96c6\u5408nodes"},"\u6307\u6807\u96c6\u5408\uff1anodes"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5355\u4f4d"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5e2e\u52a9\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"total"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u8282\u70b9\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"successful"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u5728\u7ebf\u8282\u70b9\u6570")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"failed"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u79bb\u7ebf\u8282\u70b9\u6570")))),(0,r.yg)("h4",{id:"\u6307\u6807\u96c6\u5408nodes_detail"},"\u6307\u6807\u96c6\u5408\uff1anodes_detail"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u540d\u79f0"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5355\u4f4d"),(0,r.yg)("th",{parentName:"tr",align:null},"\u6307\u6807\u5e2e\u52a9\u63cf\u8ff0"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"node_name"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"\u8282\u70b9\u540d\u79f0")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"ip"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"IP\u5730\u5740")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"cpu_load_average"),(0,r.yg)("td",{parentName:"tr",align:null},"\u65e0"),(0,r.yg)("td",{parentName:"tr",align:null},"CPU\u5e73\u5747\u8d1f\u8f7d")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"cpu_percent"),(0,r.yg)("td",{parentName:"tr",align:null},"%"),(0,r.yg)("td",{parentName:"tr",align:null},"CPU\u5360\u7528\u7387")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"heap_used"),(0,r.yg)("td",{parentName:"tr",align:null},"MB"),(0,r.yg)("td",{parentName:"tr",align:null},"\u5185\u5b58\u4f7f\u7528\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"heap_used_percent"),(0,r.yg)("td",{parentName:"tr",align:null},"%"),(0,r.yg)("td",{parentName:"tr",align:null},"\u5185\u5b58\u4f7f\u7528\u7387")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"heap_total"),(0,r.yg)("td",{parentName:"tr",align:null},"MB"),(0,r.yg)("td",{parentName:"tr",align:null},"\u603b\u5185\u5b58")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"disk_free"),(0,r.yg)("td",{parentName:"tr",align:null},"GB"),(0,r.yg)("td",{parentName:"tr",align:null},"\u78c1\u76d8\u5269\u4f59\u5bb9\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"disk_total"),(0,r.yg)("td",{parentName:"tr",align:null},"GB"),(0,r.yg)("td",{parentName:"tr",align:null},"\u78c1\u76d8\u603b\u5bb9\u91cf")),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},"disk_used_percent"),(0,r.yg)("td",{parentName:"tr",align:null},"%"),(0,r.yg)("td",{parentName:"tr",align:null},"\u78c1\u76d8\u4f7f\u7528\u7387")))))}m.isMDXComponent=!0}}]);