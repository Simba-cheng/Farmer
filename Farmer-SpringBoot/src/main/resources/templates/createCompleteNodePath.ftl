<#--创建完整节点路径-->
<div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="zkIndex.closePopUpsCreateCompleteNodePath()">
                <span aria-hidden="true">&times;</span>
            </button>
            <h4 class="modal-title" id="createCompleteNodelLabel">新建节点</h4>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label for="txt_departmentname">创建完整节点</label>
                <textarea id="createCompleteNodePath" name="content" rows="3" cols="5" placeholder="请填写完整节点路径&#10;例如：/home/search/serch1 或 /home/search/searchConfig.properties"></textarea>
            </div>
            <div class="form-group">
                <label for="txt_departmentname">节点数据</label>
                <textarea id="createCompleteNodeData" name="content" rows="3" cols="5" placeholder="请填写节点数据"></textarea>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal" onclick="zkIndex.closePopUpsCreateCompleteNodePath()">
                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>关闭
            </button>
            <button type="button" id="btn_conn_zk_submit" class="btn btn-primary" data-dismiss="modal" onclick="zkIndex.createCompleteNodePath(this)">
                <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确定
            </button>
        </div>
    </div>
</div>
