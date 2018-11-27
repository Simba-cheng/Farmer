<#--<ul>-->
<#if resultVO??>
    <#list resultVO.resultData.childNodeInfoList as childNode >
    <#--parent_li:节点是否可以点击-->
            <li <#if "1" == childNode.isExistenceChild>class="parent_li"</#if>>
                <#--默认 title="Expand this branch"(展开此分支) -->
                <span class="clickNodeMark" name="${childNode.nodePath}" id="${childNode.completeNode}" nodeIsFileValue="${childNode.nodeIsFile}" onclick="zkIndex.nodeInfoQuery(this)" title="Expand this branch">
                    <i class="icon-folder-open"></i>${childNode.nodePath}</span>
            </li>
    </#list>
</#if>
<#--</ul>-->
