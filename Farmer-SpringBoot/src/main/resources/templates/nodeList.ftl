<#--<div class="content">-->
<div class="treeMenu" id="treeMenu">
    <div class="tree well">
        <div class="nav-content-detail tree-btn" id="nodeList_createNodes">
            <button type="button" id="button_createNodes" class="btn btn-success" onclick="zkIndex.popUpsCreateCompleteNodePath()">新建节点</button>
            <#--<button type="button" id="button_createNodes" class="btn btn-warning" onclick="zkIndex.fileUpLoadPopUps()">上传文件</button>-->
        </div>
        <ul id="node_start"></ul>
    </div>

<#--节点展示页面-->
        <#include "nodeDisplay.ftl">
</div>
<#--</div>-->
