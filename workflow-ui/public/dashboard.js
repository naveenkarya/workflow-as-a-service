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
                $("#workflow-status").append(`<h3>${response.name}</h3>`);
                $("#workflow-status").append(`<h4>${statusCodeMap.get(response.status)}</h4>`);
                let k = response.length - 1;
                var html = [];
                //response.sort((a, b) => a.created - b.created);
                html.push('<ul class="list">');
                for (workflow of response) {
                    html.push(`<li class="item"> <a href = "/workflow/${workflow.id}">${workflow.id} :: ${workflow.name} :: ${statusCodeMap.get(workflow.status)}</a></li>`);
                }
                html.push('</ul>')
                $("#workflow-list").append(html.join(""));
                $('#loader').hide();
            },
            error: function (x, e) {
                console.log(e);
            }
        });
    }
    $("#create-new-form").submit(function (event) {
        event.preventDefault();
        workflowSpecId = $("#workflowSpecId").val();
        url = `/createWorkflow`;
        data = {
            workflowSpecId: workflowSpecId
        }
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(data),
            dataType: "json",
            contentType : 'application/json',
            encode: true,
            success: function (response) {
                console.log(response);
                workflowList();
            },
            error: function (x, e) {
                console.log(e);
            }
        });

    });

    workflowList();
    //setInterval(generateWF(), 30000);
});