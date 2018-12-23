<html>

<head>
    <title>Farmer - ZooKeeper UI</title>
    <link rel="stylesheet" type="text/css" href="/static/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/static/css/style.css">
    <link rel="stylesheet" type="text/css" href="/static/css/sweetalert.css">
    <link rel="stylesheet" type="text/css" href="/static/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="/static/css/toastr.css">
    <link rel="stylesheet" type="text/css" href="/static/css/highlight-8.6.default.min.css">
    <link rel="stylesheet" type="text/css" href="/static/css/fileinput.css">
    <script type="text/javascript" src="/static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/static/js/zkIndex.js"></script>
    <script type="text/javascript" src="/static/js/bootstrap.js"></script>
    <script type="text/javascript" src="/static/js/sweetalert.min.js"></script>
    <script type="text/javascript" src="/static/js/auto-line-number.js"></script>
    <script type="text/javascript" src="/static/js/toastr.js"></script>
    <script type="text/javascript" src="/static/js/BootstrapMenu.min.js"></script>
    <script type="text/javascript" src="/static/js/highlight-8.6.default.min.js"></script>
    <script type="text/javascript" src="/static/js/upload/fileinput.js"></script>
    <script>
        hljs.initHighlightingOnLoad();
    </script>
</head>

<body>

<h2>Farmer - ZooKeeper Visual Interface</h2>

<#--顶部按钮-->
<div class="nav">
    <#include "button.ftl" >
</div>

<#--连接服务器的弹窗-->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: none">
    <#include "connZKServerPopUps.ftl" >
</div>

<#--右击-添加子节点弹窗-->
<div class="modal fade" id="addChildNodeParent" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: none">
    <#include "addChildNode.ftl" >
</div>

<#-- 创建节点-弹窗-->
<div class="modal fade" id="addAllNodePath" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: none">
    <#include "createCompleteNodePath.ftl">
</div>

<#--文件上传弹窗-->
<div class="modal fade" id="upLoadFilePopUps" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: none">
    <#include "upLoadFilePopUps.ftl">
</div>

<#--节点列表-->
<div class="content">
    <#include "nodeList.ftl">
</div>

<#--页脚footer-->
<div class="footer">
    <p class="ng-copyright">Copyright© 2018-2099 ，CYX_Simba版权所有 苏ICP备000001号 </p>
</div>

<script>

    new BootstrapMenu('.clickNodeMark', {
        fetchElementData: function (e) {
            // console.log("id : " + e[0].id);
            return e;
        },
        actions: [{
            name: '添加子节点',
            onClick: function (e) {
                //添加节点弹窗-展示
                zkIndex.addChildNodePopup(e);
            }
        }, {
            name: '删除节点',
            onClick: function (e) {
                zkIndex.deleteNode(e);
            }
        }
            , {
                name: '删除节点(all child node)',
                onClick: function (e) {
                    zkIndex.deleteAllChildNode(e);
                }
            }
        ]
    });

</script>

</body>
</html>
