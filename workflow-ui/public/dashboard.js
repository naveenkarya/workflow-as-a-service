$(function () {
    const statusColorMap = new Map();
    statusColorMap.set('Completed', 'complete');
    statusColorMap.set('In_Progress', 'in-progress');
    statusColorMap.set('Not_Started', 'not-started');
    const statusCodeMap = new Map();
    statusCodeMap.set('Completed', 'Completed');
    statusCodeMap.set('In_Progress', 'In Progress');
    statusCodeMap.set('Not_Started', 'Not Started');

    function workflowList() {
        $("#workflow-list").empty();
        $('#loader').show();
        url = `/workflowList`;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (response) {
                if (response.length > 0) {
                    var html = [];
                    html.push('<table class="table"><thead><tr><th scope="col">#</th><th scope="col">Workflow</th><th scope="col">Status</th></tr></thead>');
                    html.push('<tbody>');
                    for (workflow of response) {
                        html.push(`<tr><th scope="row">${workflow.id}</th>`);
                        html.push(`<td><a href = "/workflow/${workflow.id}">${workflow.name}</a></td>`);
                        html.push(`<td>${statusCodeMap.get(workflow.status)}</a></td>`);
                        html.push('</tr>');
                    }
                    html.push('</tbody></table>');
                    $("#workflow-list").append(html.join(""));
                }
                $('#loader').hide();
            },
            error: function (x, e) {
                console.log(e);
            }
        });
    }

    function getAttrs() {
        attrs = [];
        for(i = 1; i <= 4; i++) {
            let attr = $(`#attr${i}`).val();
            let val = $(`#val${i}`).val();
            if(attr != "") {
                attrs.push({name: attr, value: val});
            }
        }
        return attrs;
    }

    $("#createWorkflowButton").click(function (event) {
        event.preventDefault();
        $(".alert").remove();
        workflowSpecId = $("#workflowSpecId").attr("data-workflowSpecId");
        if(workflowSpecId == "") {
            return;
        }
        url = `/workflow/start`;
        data = {
            workflowSpecId: workflowSpecId,
            attributes: getAttrs()
        }
        
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(data),
            dataType: "json",
            contentType: 'application/json',
            encode: true,
            success: function (response) {
                console.log(response);
                html = [];
                html.push('<div class="alert alert-primary" role="alert">');
                html.push('New Workflow created!');
                html.push('</div>');
                $("#createWorkflowForm").before(html.join(""));
                $(".attr-val input").val("");
                $("#workflowSpecId").html("Select Workflow Spec");
                workflowList();
            },
            error: function (x, e) {
                console.log(e);
            }
        });
    });    

    workflowList();

    function retrieveWorkflowSpecs() {
        url = '/workflowSpec';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (response) {
                for(workflowSpec of response) {
                    $(".dropdown-menu").append(`<li class="dropdown-item" data-workflowSpecId="${workflowSpec.id}">${workflowSpec.name}</li>`);
                }
            },
            error: function (x, e) {
                console.log(e);
            }
        });
    }
    retrieveWorkflowSpecs();

    $(document).on('click', '.dropdown-menu li', function() {
        $("#workflowSpecId").attr("data-workflowSpecId", $(this).attr("data-workflowSpecId"));
        $("#workflowSpecId").html($(this).html());
    });
    //setInterval(generateWF(), 30000);
});