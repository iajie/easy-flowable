"use strict";(self.webpackChunk=self.webpackChunk||[]).push([[88],{86615:function(je,G,r){var E=r(1413),P=r(91),Y=r(22270),f=r(78045),H=r(67294),M=r(83607),Q=r(31649),U=r(85893),o=["fieldProps","options","radioType","layout","proFieldProps","valueEnum"],q=H.forwardRef(function(T,Z){var K=T.fieldProps,_=T.options,pe=T.radioType,A=T.layout,j=T.proFieldProps,y=T.valueEnum,ee=(0,P.Z)(T,o);return(0,U.jsx)(Q.Z,(0,E.Z)((0,E.Z)({valueType:pe==="button"?"radioButton":"radio",ref:Z,valueEnum:(0,Y.h)(y,void 0)},ee),{},{fieldProps:(0,E.Z)({options:_,layout:A},K),proFieldProps:j,filedConfig:{customLightMode:!0}}))}),w=H.forwardRef(function(T,Z){var K=T.fieldProps,_=T.children;return(0,U.jsx)(f.ZP,(0,E.Z)((0,E.Z)({},K),{},{ref:Z,children:_}))}),k=(0,M.G)(w,{valuePropName:"checked",ignoreWidth:!0}),D=k;D.Group=q,D.Button=f.ZP.Button,D.displayName="ProFormComponent",G.Z=D},90672:function(je,G,r){var E=r(1413),P=r(91),Y=r(67294),f=r(31649),H=r(85893),M=["fieldProps","proFieldProps"],Q=function(o,q){var w=o.fieldProps,k=o.proFieldProps,D=(0,P.Z)(o,M);return(0,H.jsx)(f.Z,(0,E.Z)({ref:q,valueType:"textarea",fieldProps:w,proFieldProps:k},D))};G.Z=Y.forwardRef(Q)},32443:function(je,G,r){r.d(G,{Z:function(){return A}});var E=r(1413),P=r(91),Y=r(87462),f=r(67294),H={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M400 317.7h73.9V656c0 4.4 3.6 8 8 8h60c4.4 0 8-3.6 8-8V317.7H624c6.7 0 10.4-7.7 6.3-12.9L518.3 163a8 8 0 00-12.6 0l-112 141.7c-4.1 5.3-.4 13 6.3 13zM878 626h-60c-4.4 0-8 3.6-8 8v154H214V634c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8v198c0 17.7 14.3 32 32 32h684c17.7 0 32-14.3 32-32V634c0-4.4-3.6-8-8-8z"}}]},name:"upload",theme:"outlined"},M=H,Q=r(84089),U=function(y,ee){return f.createElement(Q.Z,(0,Y.Z)({},y,{ref:ee,icon:M}))},o=f.forwardRef(U),q=o,w=r(31365),k=r(14726),D=r(9105),T=r(83607),Z=r(85893),K=["fieldProps","action","accept","listType","title","max","icon","buttonProps","disabled","proFieldProps"],_=function(y,ee){var ge,$=y.fieldProps,ne=y.action,se=y.accept,oe=y.listType,xe=y.title,Ce=xe===void 0?"\u5355\u51FB\u4E0A\u4F20":xe,R=y.max,Se=y.icon,ie=Se===void 0?(0,Z.jsx)(q,{}):Se,Ie=y.buttonProps,Oe=y.disabled,he=y.proFieldProps,le=(0,P.Z)(y,K),de=(0,f.useMemo)(function(){var ce;return(ce=le.fileList)!==null&&ce!==void 0?ce:le.value},[le.fileList,le.value]),Pe=(0,f.useContext)(D.A),d=(he==null?void 0:he.mode)||Pe.mode||"edit",L=(R===void 0||!de||(de==null?void 0:de.length)<R)&&d!=="read",Ee=(oe!=null?oe:$==null?void 0:$.listType)==="picture-card";return(0,Z.jsx)(w.Z,(0,E.Z)((0,E.Z)({action:ne,accept:se,ref:ee,listType:oe||"picture",fileList:de},$),{},{name:(ge=$==null?void 0:$.name)!==null&&ge!==void 0?ge:"file",onChange:function(Te){var be;$==null||(be=$.onChange)===null||be===void 0||be.call($,Te)},children:L&&(Ee?(0,Z.jsxs)("span",{children:[ie," ",Ce]}):(0,Z.jsxs)(k.ZP,(0,E.Z)((0,E.Z)({disabled:Oe||($==null?void 0:$.disabled)},Ie),{},{children:[ie,Ce]})))}))},pe=(0,T.G)(f.forwardRef(_),{getValueFromEvent:function(y){return y.fileList}}),A=pe},36321:function(je,G,r){r.r(G),r.d(G,{default:function(){return st}});var E=r(97857),P=r.n(E),Y=r(15009),f=r.n(Y),H=r(99289),M=r.n(H),Q=r(5574),U=r.n(Q),o=r(67294),q=r(57349),w=r(59530),k=r(184),D=r(37476),T=r(90672),Z=r(7529),K=r(88859),_=r(90930),pe=r(51390),A=r(71613),j=r(66309),y=function(){var e=M()(f()().mark(function t(n){var a,s,v;return f()().wrap(function(c){for(;;)switch(c.prev=c.next){case 0:if(n.processDefinitionId){c.next=2;break}return c.abrupt("return",{success:!1,data:[]});case 2:return c.next=4,(0,A.pb)(n.processDefinitionId);case 4:return a=c.sent,s=a.success,v=a.result,c.abrupt("return",{success:s,data:v||[]});case 8:case"end":return c.stop()}},t)}));return function(n){return e.apply(this,arguments)}}(),ee=function(){var e=M()(f()().mark(function t(n){var a,s,v;return f()().wrap(function(c){for(;;)switch(c.prev=c.next){case 0:return c.next=2,(0,A.Ud)(n.processInstanceId);case 2:return a=c.sent,s=a.success,v=a.result,c.abrupt("return",{success:s,data:v||[]});case 6:case"end":return c.stop()}},t)}));return function(n){return e.apply(this,arguments)}}(),ge=function(t){return[{valueType:"index",title:"\u5E8F\u53F7",width:60},{dataIndex:"name",title:"\u5B9E\u4F8B\u540D\u79F0",width:160,ellipsis:!0},{dataIndex:"businessKey",align:"center",title:"\u4E1A\u52A1\u4E3B\u952E",width:140,ellipsis:!0},{dataIndex:"startUserId",align:"center",title:"\u53D1\u8D77\u4EBA",width:120,valueType:"select",request:function(){return t}},{dataIndex:"processInstanceVersion",title:"\u6D41\u7A0B\u5B9E\u4F8B\u8FD0\u884C\u7248\u672C",align:"center",render:function(a){return o.createElement(j.Z,{color:"success"},"V "+a)}},{dataIndex:"businessKeyStatus",align:"center",ellipsis:!0,title:"\u4E1A\u52A1\u72B6\u6001"},{dataIndex:"status",title:"\u6D41\u7A0B\u5B9E\u4F8B\u72B6\u6001",align:"center",width:120,render:function(a,s){var v=s.status;return o.createElement(j.Z,{color:v?"red":"success"},v?"\u6302\u8D77":"\u6FC0\u6D3B")}}]},$=r(78957),ne=r(14726),se=r(26412),oe=r(17788),xe=r(55241),Ce=r(93967),R=r.n(Ce),Se=r(98423),ie=r(53124),Ie=r(98675),Oe=r(48054),he=r(92398),le=function(e,t){var n={};for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&t.indexOf(a)<0&&(n[a]=e[a]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var s=0,a=Object.getOwnPropertySymbols(e);s<a.length;s++)t.indexOf(a[s])<0&&Object.prototype.propertyIsEnumerable.call(e,a[s])&&(n[a[s]]=e[a[s]]);return n},Pe=e=>{var{prefixCls:t,className:n,hoverable:a=!0}=e,s=le(e,["prefixCls","className","hoverable"]);const{getPrefixCls:v}=o.useContext(ie.E_),m=v("card",t),c=R()(`${m}-grid`,n,{[`${m}-grid-hoverable`]:a});return o.createElement("div",Object.assign({},s,{className:c}))},d=r(11568),L=r(14747),Ee=r(83559),ce=r(83262);const Te=e=>{const{antCls:t,componentCls:n,headerHeight:a,cardPaddingBase:s,tabsMarginBottom:v}=e;return Object.assign(Object.assign({display:"flex",justifyContent:"center",flexDirection:"column",minHeight:a,marginBottom:-1,padding:`0 ${(0,d.bf)(s)}`,color:e.colorTextHeading,fontWeight:e.fontWeightStrong,fontSize:e.headerFontSize,background:e.headerBg,borderBottom:`${(0,d.bf)(e.lineWidth)} ${e.lineType} ${e.colorBorderSecondary}`,borderRadius:`${(0,d.bf)(e.borderRadiusLG)} ${(0,d.bf)(e.borderRadiusLG)} 0 0`},(0,L.dF)()),{"&-wrapper":{width:"100%",display:"flex",alignItems:"center"},"&-title":Object.assign(Object.assign({display:"inline-block",flex:1},L.vS),{[`
          > ${n}-typography,
          > ${n}-typography-edit-content
        `]:{insetInlineStart:0,marginTop:0,marginBottom:0}}),[`${t}-tabs-top`]:{clear:"both",marginBottom:v,color:e.colorText,fontWeight:"normal",fontSize:e.fontSize,"&-bar":{borderBottom:`${(0,d.bf)(e.lineWidth)} ${e.lineType} ${e.colorBorderSecondary}`}}})},be=e=>{const{cardPaddingBase:t,colorBorderSecondary:n,cardShadow:a,lineWidth:s}=e;return{width:"33.33%",padding:t,border:0,borderRadius:0,boxShadow:`
      ${(0,d.bf)(s)} 0 0 0 ${n},
      0 ${(0,d.bf)(s)} 0 0 ${n},
      ${(0,d.bf)(s)} ${(0,d.bf)(s)} 0 0 ${n},
      ${(0,d.bf)(s)} 0 0 0 ${n} inset,
      0 ${(0,d.bf)(s)} 0 0 ${n} inset;
    `,transition:`all ${e.motionDurationMid}`,"&-hoverable:hover":{position:"relative",zIndex:1,boxShadow:a}}},ze=e=>{const{componentCls:t,iconCls:n,actionsLiMargin:a,cardActionsIconSize:s,colorBorderSecondary:v,actionsBg:m}=e;return Object.assign(Object.assign({margin:0,padding:0,listStyle:"none",background:m,borderTop:`${(0,d.bf)(e.lineWidth)} ${e.lineType} ${v}`,display:"flex",borderRadius:`0 0 ${(0,d.bf)(e.borderRadiusLG)} ${(0,d.bf)(e.borderRadiusLG)}`},(0,L.dF)()),{"& > li":{margin:a,color:e.colorTextDescription,textAlign:"center","> span":{position:"relative",display:"block",minWidth:e.calc(e.cardActionsIconSize).mul(2).equal(),fontSize:e.fontSize,lineHeight:e.lineHeight,cursor:"pointer","&:hover":{color:e.colorPrimary,transition:`color ${e.motionDurationMid}`},[`a:not(${t}-btn), > ${n}`]:{display:"inline-block",width:"100%",color:e.colorTextDescription,lineHeight:(0,d.bf)(e.fontHeight),transition:`color ${e.motionDurationMid}`,"&:hover":{color:e.colorPrimary}},[`> ${n}`]:{fontSize:s,lineHeight:(0,d.bf)(e.calc(s).mul(e.lineHeight).equal())}},"&:not(:last-child)":{borderInlineEnd:`${(0,d.bf)(e.lineWidth)} ${e.lineType} ${v}`}}})},Le=e=>Object.assign(Object.assign({margin:`${(0,d.bf)(e.calc(e.marginXXS).mul(-1).equal())} 0`,display:"flex"},(0,L.dF)()),{"&-avatar":{paddingInlineEnd:e.padding},"&-detail":{overflow:"hidden",flex:1,"> div:not(:last-child)":{marginBottom:e.marginXS}},"&-title":Object.assign({color:e.colorTextHeading,fontWeight:e.fontWeightStrong,fontSize:e.fontSizeLG},L.vS),"&-description":{color:e.colorTextDescription}}),We=e=>{const{componentCls:t,cardPaddingBase:n,colorFillAlter:a}=e;return{[`${t}-head`]:{padding:`0 ${(0,d.bf)(n)}`,background:a,"&-title":{fontSize:e.fontSize}},[`${t}-body`]:{padding:`${(0,d.bf)(e.padding)} ${(0,d.bf)(n)}`}}},Ne=e=>{const{componentCls:t}=e;return{overflow:"hidden",[`${t}-body`]:{userSelect:"none"}}},Ge=e=>{const{componentCls:t,cardShadow:n,cardHeadPadding:a,colorBorderSecondary:s,boxShadowTertiary:v,cardPaddingBase:m,extraColor:c}=e;return{[t]:Object.assign(Object.assign({},(0,L.Wf)(e)),{position:"relative",background:e.colorBgContainer,borderRadius:e.borderRadiusLG,[`&:not(${t}-bordered)`]:{boxShadow:v},[`${t}-head`]:Te(e),[`${t}-extra`]:{marginInlineStart:"auto",color:c,fontWeight:"normal",fontSize:e.fontSize},[`${t}-body`]:Object.assign({padding:m,borderRadius:`0 0 ${(0,d.bf)(e.borderRadiusLG)} ${(0,d.bf)(e.borderRadiusLG)}`},(0,L.dF)()),[`${t}-grid`]:be(e),[`${t}-cover`]:{"> *":{display:"block",width:"100%",borderRadius:`${(0,d.bf)(e.borderRadiusLG)} ${(0,d.bf)(e.borderRadiusLG)} 0 0`}},[`${t}-actions`]:ze(e),[`${t}-meta`]:Le(e)}),[`${t}-bordered`]:{border:`${(0,d.bf)(e.lineWidth)} ${e.lineType} ${s}`,[`${t}-cover`]:{marginTop:-1,marginInlineStart:-1,marginInlineEnd:-1}},[`${t}-hoverable`]:{cursor:"pointer",transition:`box-shadow ${e.motionDurationMid}, border-color ${e.motionDurationMid}`,"&:hover":{borderColor:"transparent",boxShadow:n}},[`${t}-contain-grid`]:{borderRadius:`${(0,d.bf)(e.borderRadiusLG)} ${(0,d.bf)(e.borderRadiusLG)} 0 0 `,[`${t}-body`]:{display:"flex",flexWrap:"wrap"},[`&:not(${t}-loading) ${t}-body`]:{marginBlockStart:e.calc(e.lineWidth).mul(-1).equal(),marginInlineStart:e.calc(e.lineWidth).mul(-1).equal(),padding:0}},[`${t}-contain-tabs`]:{[`> div${t}-head`]:{minHeight:0,[`${t}-head-title, ${t}-extra`]:{paddingTop:a}}},[`${t}-type-inner`]:We(e),[`${t}-loading`]:Ne(e),[`${t}-rtl`]:{direction:"rtl"}}},He=e=>{const{componentCls:t,cardPaddingSM:n,headerHeightSM:a,headerFontSizeSM:s}=e;return{[`${t}-small`]:{[`> ${t}-head`]:{minHeight:a,padding:`0 ${(0,d.bf)(n)}`,fontSize:s,[`> ${t}-head-wrapper`]:{[`> ${t}-extra`]:{fontSize:e.fontSize}}},[`> ${t}-body`]:{padding:n}},[`${t}-small${t}-contain-tabs`]:{[`> ${t}-head`]:{[`${t}-head-title, ${t}-extra`]:{paddingTop:0,display:"flex",alignItems:"center"}}}}},Ue=e=>({headerBg:"transparent",headerFontSize:e.fontSizeLG,headerFontSizeSM:e.fontSize,headerHeight:e.fontSizeLG*e.lineHeightLG+e.padding*2,headerHeightSM:e.fontSize*e.lineHeight+e.paddingXS*2,actionsBg:e.colorBgContainer,actionsLiMargin:`${e.paddingSM}px 0`,tabsMarginBottom:-e.padding-e.lineWidth,extraColor:e.colorText});var we=(0,Ee.I$)("Card",e=>{const t=(0,ce.IX)(e,{cardShadow:e.boxShadowCard,cardHeadPadding:e.padding,cardPaddingBase:e.paddingLG,cardActionsIconSize:e.fontSize,cardPaddingSM:12});return[Ge(t),He(t)]},Ue),Me=function(e,t){var n={};for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&t.indexOf(a)<0&&(n[a]=e[a]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var s=0,a=Object.getOwnPropertySymbols(e);s<a.length;s++)t.indexOf(a[s])<0&&Object.prototype.propertyIsEnumerable.call(e,a[s])&&(n[a[s]]=e[a[s]]);return n};const Ke=e=>{const{actionClasses:t,actions:n=[],actionStyle:a}=e;return o.createElement("ul",{className:t,style:a},n.map((s,v)=>{const m=`action-${v}`;return o.createElement("li",{style:{width:`${100/n.length}%`},key:m},o.createElement("span",null,s))}))};var Ve=o.forwardRef((e,t)=>{const{prefixCls:n,className:a,rootClassName:s,style:v,extra:m,headStyle:c={},bodyStyle:W={},title:V,loading:X,bordered:ue=!0,size:ve,type:te,cover:J,actions:re,tabList:ae,children:F,activeTabKey:z,defaultActiveTabKey:h,tabBarExtraContent:I,hoverable:b,tabProps:u={},classNames:i,styles:p}=e,g=Me(e,["prefixCls","className","rootClassName","style","extra","headStyle","bodyStyle","title","loading","bordered","size","type","cover","actions","tabList","children","activeTabKey","defaultActiveTabKey","tabBarExtraContent","hoverable","tabProps","classNames","styles"]),{getPrefixCls:N,direction:ye,card:x}=o.useContext(ie.E_),fe=O=>{var C;(C=e.onTabChange)===null||C===void 0||C.call(e,O)},B=O=>{var C;return R()((C=x==null?void 0:x.classNames)===null||C===void 0?void 0:C[O],i==null?void 0:i[O])},me=O=>{var C;return Object.assign(Object.assign({},(C=x==null?void 0:x.styles)===null||C===void 0?void 0:C[O]),p==null?void 0:p[O])},ot=o.useMemo(()=>{let O=!1;return o.Children.forEach(F,C=>{(C==null?void 0:C.type)===Pe&&(O=!0)}),O},[F]),S=N("card",n),[it,lt,dt]=we(S),ct=o.createElement(Oe.Z,{loading:!0,active:!0,paragraph:{rows:4},title:!1},F),De=z!==void 0,ut=Object.assign(Object.assign({},u),{[De?"activeKey":"defaultActiveKey"]:De?z:h,tabBarExtraContent:I});let Ze;const $e=(0,Ie.Z)(ve),vt=!$e||$e==="default"?"large":$e,Ae=ae?o.createElement(he.Z,Object.assign({size:vt},ut,{className:`${S}-head-tabs`,onChange:fe,items:ae.map(O=>{var{tab:C}=O,Be=Me(O,["tab"]);return Object.assign({label:C},Be)})})):null;if(V||m||Ae){const O=R()(`${S}-head`,B("header")),C=R()(`${S}-head-title`,B("title")),Be=R()(`${S}-extra`,B("extra")),St=Object.assign(Object.assign({},c),me("header"));Ze=o.createElement("div",{className:O,style:St},o.createElement("div",{className:`${S}-head-wrapper`},V&&o.createElement("div",{className:C,style:me("title")},V),m&&o.createElement("div",{className:Be,style:me("extra")},m)),Ae)}const ft=R()(`${S}-cover`,B("cover")),mt=J?o.createElement("div",{className:ft,style:me("cover")},J):null,pt=R()(`${S}-body`,B("body")),gt=Object.assign(Object.assign({},W),me("body")),ht=o.createElement("div",{className:pt,style:gt},X?ct:F),bt=R()(`${S}-actions`,B("actions")),yt=re!=null&&re.length?o.createElement(Ke,{actionClasses:bt,actionStyle:me("actions"),actions:re}):null,$t=(0,Se.Z)(g,["onTabChange"]),xt=R()(S,x==null?void 0:x.className,{[`${S}-loading`]:X,[`${S}-bordered`]:ue,[`${S}-hoverable`]:b,[`${S}-contain-grid`]:ot,[`${S}-contain-tabs`]:ae==null?void 0:ae.length,[`${S}-${$e}`]:$e,[`${S}-type-${te}`]:!!te,[`${S}-rtl`]:ye==="rtl"},a,s,lt,dt),Ct=Object.assign(Object.assign({},x==null?void 0:x.style),v);return it(o.createElement("div",Object.assign({ref:t},$t,{className:xt,style:Ct}),Ze,mt,ht,yt))}),Xe=function(e,t){var n={};for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&t.indexOf(a)<0&&(n[a]=e[a]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var s=0,a=Object.getOwnPropertySymbols(e);s<a.length;s++)t.indexOf(a[s])<0&&Object.prototype.propertyIsEnumerable.call(e,a[s])&&(n[a[s]]=e[a[s]]);return n},Je=e=>{const{prefixCls:t,className:n,avatar:a,title:s,description:v}=e,m=Xe(e,["prefixCls","className","avatar","title","description"]),{getPrefixCls:c}=o.useContext(ie.E_),W=c("card",t),V=R()(`${W}-meta`,n),X=a?o.createElement("div",{className:`${W}-meta-avatar`},a):null,ue=s?o.createElement("div",{className:`${W}-meta-title`},s):null,ve=v?o.createElement("div",{className:`${W}-meta-description`},v):null,te=ue||ve?o.createElement("div",{className:`${W}-meta-detail`},ue,ve):null;return o.createElement("div",Object.assign({},m,{className:V}),X,te)};const Fe=Ve;Fe.Grid=Pe,Fe.Meta=Je;var Ye=Fe,Qe=r(7134),Re=r(60849),qe=r(87462),ke={icon:function(t,n){return{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M534 352V136H232v752h560V394H576a42 42 0 01-42-42zm-134 50c22.1 0 40 17.9 40 40s-17.9 40-40 40-40-17.9-40-40 17.9-40 40-40zm296 294H328.1c-6.7 0-10.4-7.7-6.3-12.9l99.8-127.2a8 8 0 0112.6 0l41.1 52.4 77.8-99.2a8.1 8.1 0 0112.7 0l136.5 174c4.1 5.2.4 12.9-6.3 12.9z",fill:n}},{tag:"path",attrs:{d:"M854.6 288.6L639.4 73.4c-6-6-14.1-9.4-22.6-9.4H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V311.3c0-8.5-3.4-16.7-9.4-22.7zM602 137.8L790.2 326H602V137.8zM792 888H232V136h302v216a42 42 0 0042 42h216v494z",fill:t}},{tag:"path",attrs:{d:"M553.1 509.1l-77.8 99.2-41.1-52.4a8 8 0 00-12.6 0l-99.8 127.2a7.98 7.98 0 006.3 12.9H696c6.7 0 10.4-7.7 6.3-12.9l-136.5-174a8.1 8.1 0 00-12.7 0zM360 442a40 40 0 1080 0 40 40 0 10-80 0z",fill:t}}]}},name:"file-image",theme:"twotone"},_e=ke,et=r(84089),tt=function(t,n){return o.createElement(et.Z,(0,qe.Z)({},t,{ref:n,icon:_e}))},rt=o.forwardRef(tt),at=rt,nt=r(96944),l=r(85893),st=function(){var e=(0,w.useModel)("@@initialState"),t=e.initialState,n=o.useRef(),a=t.users,s=t.groups,v=(0,w.useLocation)(),m=v.state,c=o.useRef(),W=o.useState(!1),V=U()(W,2),X=V[0],ue=V[1],ve=o.useState({open:!1,processInstanceId:"",processDefinitionId:""}),te=U()(ve,2),J=te[0],re=te[1];o.useEffect(function(){if(!X){var F;(F=c.current)===null||F===void 0||F.reloadAndRest()}},[X]);var ae=[{title:"\u64CD\u4F5C",align:"center",render:function(z,h){return(0,l.jsxs)($.Z,{children:[(0,l.jsx)(ne.ZP,{onClick:function(){return re({open:!0,processInstanceId:h.processInstanceId,processDefinitionId:h.processDefinitionId})},type:"link",icon:(0,l.jsx)(at,{}),children:"\u6D41\u7A0B\u52A8\u6001"}),(0,l.jsx)(ne.ZP,{onClick:M()(f()().mark(function I(){var b,u,i;return f()().wrap(function(g){for(;;)switch(g.prev=g.next){case 0:return g.next=2,(0,A.xg)(h.processInstanceId);case 2:b=g.sent,u=b.success,u&&((i=c.current)===null||i===void 0||i.reloadAndRest());case 5:case"end":return g.stop()}},I)})),style:{color:h.status?"#87d068":"#ff4d4f"},type:"text",children:h.status?"\u6FC0\u6D3B":"\u6302\u8D77"}),(0,l.jsx)(k.a,{title:h.name,open:X,onOpenChange:function(){var b;return(b=c.current)===null||b===void 0?void 0:b.reloadAndRest()},drawerProps:{destroyOnClose:!0},submitter:{resetButtonProps:!1,searchConfig:{submitText:"\u6267\u884C"},render:function(b,u){return(0,l.jsxs)($.Z,{children:[(0,l.jsx)(Re.z,P()(P()({},h),{},{reload:function(){var p,g;(p=c.current)===null||p===void 0||p.reloadAndRest(),(g=n.current)===null||g===void 0||g.reloadAndRest()}})),(0,l.jsx)(D.Y,{width:"25%",onFinish:function(){var i=M()(f()().mark(function p(g){var N,ye,x;return f()().wrap(function(B){for(;;)switch(B.prev=B.next){case 0:return B.next=2,(0,A.t8)(P()(P()({},g),{},{flowCommentType:"CANCELLATION",processInstanceId:h.processInstanceId,taskId:h.taskId}));case 2:return ye=B.sent,x=ye.success,ue(!1),(N=c.current)===null||N===void 0||N.reloadAndRest(),B.abrupt("return",x);case 7:case"end":return B.stop()}},p)}));return function(p){return i.apply(this,arguments)}}(),trigger:(0,l.jsx)(ne.ZP,{disabled:h.status||!h.taskId,type:"primary",danger:!0,children:"\u6D41\u7A0B\u4F5C\u5E9F"}),children:(0,l.jsx)(T.Z,{name:"commentContent",label:"\u4F5C\u5E9F\u539F\u56E0",rules:[{required:!0,message:"\u4F5C\u5E9F\u539F\u56E0\u4E0D\u80FD\u4E3A\u7A7A"}]})})]})}},trigger:(0,l.jsx)(ne.ZP,{type:"text",style:{color:"purple"},children:"\u6267\u884C\u5386\u53F2"}),children:(0,l.jsx)(Z.Rs,{grid:{column:1},actionRef:n,params:{processInstanceId:h.processInstanceId},metas:{avatar:{render:function(b,u){var i=u.duration,p=u.assignee;if(i||i===0){if(i<1e3)return(0,l.jsxs)(j.Z,{color:"success",children:["\u8017\u65F6\uFF1A",i,"\u6BEB\u79D2"]});if(i>=1e3&&i<60*1e3)return(0,l.jsxs)(j.Z,{color:"success",children:["\u8017\u65F6\uFF1A",i/1e3,"\u79D2"]});if(i>=60*1e3&&i<60*60*1e3)return(0,l.jsxs)(j.Z,{color:"success",children:["\u8017\u65F6\uFF1A",(i/(60*1e3)).toFixed(0),"\u5206\u949F"]});if(i>=60*60*1e3&&i<24*60*60*1e3)return(0,l.jsxs)(j.Z,{color:"success",children:["\u8017\u65F6\uFF1A",(i/(60*60*1e3)).toFixed(2),"\u5C0F\u65F6"]});if(i>=24*60*60*1e3)return(0,l.jsxs)(j.Z,{color:"warning",children:["\u8017\u65F6\uFF1A",(i/(24*60*60*1e3)).toFixed(2),"\u5929"]})}return(0,l.jsx)(j.Z,{color:"orange",children:p?"\u5F85\u529E":"\u4EFB\u52A1\u5F85\u7B7E\u6536"})}},title:{dataIndex:"taskName"},subTitle:{dataIndex:"assignee",render:function(b,u){var i=u.assignee,p=u.candidateUsers,g=u.candidateGroups;return g&&g.length?(0,l.jsxs)(j.Z,{color:"purple",children:["\u5019\u9009\u7EC4:",(0,l.jsx)(K.Ju,{text:g,mode:"read",request:function(){return s}})]}):(0,l.jsxs)(j.Z,{color:"purple",children:[i?"\u6267\u884C\u4EBA: ":"\u5019\u9009\u4EBA: ",(0,l.jsx)(K.Ju,{text:i!=null?i:p,mode:"read",request:function(){return a}})]})}},content:{render:function(b,u){return(0,l.jsxs)(se.Z,{column:3,children:[(0,l.jsx)(se.Z.Item,{label:"\u4EFB\u52A1\u8282\u70B9",children:u.taskDefKey}),(0,l.jsx)(se.Z.Item,{label:"\u5F00\u59CB\u65F6\u95F4",children:u.startTime}),(0,l.jsx)(se.Z.Item,{label:"\u7ED3\u675F\u65F6\u95F4",children:u.endTime})]})}},actions:{render:function(b,u){var i=u.comments,p=u.taskName,g=u.taskId,N=u.endTime;return(0,l.jsxs)($.Z,{children:[i&&i.length?(0,l.jsx)(Re.K,{comments:i,taskName:p}):null,h.taskIds&&!N?(0,l.jsx)(Re.z,P()(P()({},h),{},{link:!0,taskId:g,reload:function(){var x,fe;(x=c.current)===null||x===void 0||x.reloadAndRest(),(fe=n.current)===null||fe===void 0||fe.reloadAndRest()}})):null]})}}},rowKey:"historyId",request:ee})})]})}}];return(0,l.jsxs)(_._z,{children:[(0,l.jsx)(pe.Z,{headerTitle:(m==null?void 0:m.name)||"\u6D41\u7A0B\u8FD0\u884C\u5B9E\u4F8B\u5217\u8868",params:{processDefinitionId:m==null?void 0:m.processDefinitionId},request:y,search:!1,scroll:{y:600},columns:ge(a).concat(ae),pagination:!1,rowKey:"processInstanceId",actionRef:c}),(0,l.jsx)(oe.Z,{open:J.open,width:"60%",centered:!0,destroyOnClose:!0,keyboard:!0,mask:!1,footer:!1,onCancel:function(){return re(P()(P()({},J),{},{open:!1}))},children:(0,l.jsx)(nt.s9,{mode:"active",height:70,onClick:function(){var F=M()(f()().mark(function z(h){var I,b;return f()().wrap(function(i){for(;;)switch(i.prev=i.next){case 0:return i.next=2,(0,A.hM)(h);case 2:return I=i.sent,b=I.result,i.abrupt("return",P()(P()({},b),{},{users:(0,l.jsx)($.Z,{children:((b==null?void 0:b.users)||[]).map(function(p,g){return(0,l.jsx)(xe.Z,{title:"\u6267\u884C\u4EBA",content:(0,l.jsxs)(Ye,{title:!1,children:[(0,l.jsx)(Qe.C,{src:p.avatar||q}),(0,l.jsxs)("span",{children:["\u90AE\u7BB1\uFF1A",p.email]}),(0,l.jsx)("br",{})]}),children:(0,l.jsx)(j.Z,{color:"warning",children:p.username})},"user_"+g)})})}));case 5:case"end":return i.stop()}},z)}));return function(z){return F.apply(this,arguments)}}(),request:M()(f()().mark(function F(){var z,h,I;return f()().wrap(function(u){for(;;)switch(u.prev=u.next){case 0:if(!J.open){u.next=7;break}return u.next=3,(0,A.ZZ)({processInstanceId:J.processInstanceId,processDefinitionId:J.processDefinitionId});case 3:return z=u.sent,h=z.success,I=z.result,u.abrupt("return",I);case 7:return u.abrupt("return","");case 8:case"end":return u.stop()}},F)})),excludeType:["SequenceFlow","InclusiveGateway"]})})]})}}}]);
