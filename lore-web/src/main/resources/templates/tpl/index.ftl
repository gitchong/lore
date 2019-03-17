<#include "/includes/taglib.ftl" />
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!--> <html lang="en" class="no-js"> <!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="referrer" content="origin" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=1100, maximum-scale=1.0, user-scalable=yes" />
    <link rel="shortcut icon" href="//www.aliyun.com/favicon.ico" type="image/x-icon" />
    <base href="${contextPath}" id="baseRoot"/>
    <link rel="stylesheet" type="text/css" href="${contextPath}/modules/fontawesome/4.7.0/css/font-awesome.min.css?v=${ver!'1'}" />
    <link rel="stylesheet" type="text/css" href="${contextPath}/modules/nprogress/nprogress.css?v=${ver!'1'}" />
    <link rel="stylesheet" type="text/css" href="${contextPath}/css/animate.css?v=${ver!'1'}" />
    <title><@spring.message  code="label.system.name" />-<@spring.message  code="label.system.index" /></title>
    <meta name="description" content="<@spring.message  code="label.system.name" />,为您提供愉悦的资料分享体验!" />
    <meta name="Keywords" content="公文，公文库，ppt模板下载，简历模板，PPT定制，视频教程，图片素材下载，ppt素材库、工作总结、工作计划、调研报告、竞聘演讲稿、会议发言稿、学习心得体会、入党申请书、应用公文、论文范文、范文写作技巧" />
    <link rel="stylesheet" href="${contextPath}/modules/bootstrap/3.3.5/css/bootstrap.min.css?v=${ver!'1'}">
    <link href="${contextPath}/css/index.css?v=${ver!'1'}" type="text/css" rel="stylesheet">
    <link href="${contextPath}/css/main.css?v=${ver!'1'}" type="text/css" rel="stylesheet">
    <link href="${contextPath}/css/style.css?v=${ver!'1'}" type="text/css" rel="stylesheet">
    <style>
        a {
            color: #666;
            text-decoration: none;
        }
        a:hover {
            cursor:pointer;
            text-decoration: none;
        }
    </style>
</head>
<body id="top">
<!-- toolbar begin-->
<#include "/index-toolbar.ftl"/>
<!-- toolbar end-->
<!-----------------------------------------顶部-------------->
<!-- header begin-->
<#include "/index-header.ftl" />
<!-- header end-->
<!-- banner 广告 begin-->
<#include "/index-top-banner.ftl" />
<!-- banner 广告 end-->
<!-- 搜索框 begin-->
<#include "index-top-search.ftl" />
<!-- 搜索框 end-->
<!--轮播图上方导航栏  一栏-->
<#include "index-nav-top.ftl" />
<#include "index-nav-left.ftl" />
<!------------------------------------轮播图------------------------------------>
<div id="lunbo">
    <ul id="one">
        <li><a href=""><img src="//m.360buyimg.com/babel/jfs/t1/26591/24/9784/85598/5c81ec7fEa8412e69/eb35bc03d4257c41.jpg"></a></li>
        <li><a href=""><img src="//m.360buyimg.com/babel/jfs/t1/11504/11/10668/100795/5c8214cbEee7a36fa/dda4b4d65de0dd91.jpg"></a></li>
        <li><a href=""><img src="//img1.360buyimg.com/pop/jfs/t1/1778/18/9571/74609/5bad19a8Eb0489abb/329eb689314d9123.jpg" alt="一带一路”建设的成就与挑战"></a></li>
    </ul>
    <ul id="two">
        <#list [1,2,3] as img>
            <li class="<#if img_index==0>on</#if>" >${img_index+1}</li>
        </#list>
    </ul>
    <!------------------------------------轮播图左右箭头------------------------>
    <div class="slider-page">
        <a href="javascript:void(0)" id="left"><</a>
        <a href="javascript:void(0)" id="right">></a>
    </div>
</div>
<div class="m1" style="">
    <div id="J_rec" class="J_rec rec">
        <div class="rec_inner">
            <div class="rec_item">
                <a class="rec_lk mod_loading" href="" target="_blank" >
                    <div class="lazyimg lazyimg_loaded J_rec_img rec_img"><img src="/images/abd.jpg" class="lazyimg_img"></div>
                </a>
            </div>
            <div class="rec_item">
                <a class="rec_lk mod_loading" href="#" target="_blank" >
                    <div class="lazyimg lazyimg_loaded J_rec_img rec_img"><img src="/images/abd.jpg" class="lazyimg_img"></div>
                </a>
            </div>
            <div class="rec_item">
                <a class="rec_lk mod_loading" href="" target="_blank" >
                    <div class="lazyimg lazyimg_loaded J_rec_img rec_img"><img src="/images/abd.jpg" class="lazyimg_img"></div>
                </a>
            </div>
        </div>
    </div>
</div>
<!------------------------------------轮播图右侧一栏------------------------>
<div class="m">
    <div class="mt" style="padding-top: 5px;">
        <h3>天天快报</h3>
        <div class="extra"><a href="${contextPath}/articles.jhtml">更多 ></a></div>
    </div>
    <#if articles??&&articles.content??>
        <#list articles.content as article>
            <div class="mc">
                <ul>
                    <li><a href="${contextPath}/article/${article.id}.jhtml" ><span>[${article.type}]</span>${article.shortTitle}</a></li>
                </ul>
            </div>
        </#list>
    </#if>
</div>

<#include "index-main.ftl" />
</div>
<!------footer信息 begin----->
<#include "/includes/footer.ftl" />
<#include "/includes/footer-js.ftl" />
<!------footer信息 end----->
<!------seajs.config 引用信息 begin----->
<#include "/includes/seajs.config.ftl" />
<!------seajs.config   引用信息 end----->
</body>
<#include "/includes/jquery.ftl" />
<script src="js/index.js?v=${ver!'1'}"></script>
</html>