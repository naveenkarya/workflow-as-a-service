$(function () {
    const statusColorMap = new Map();
    statusColorMap.set('COMPLETED', 'complete');
    statusColorMap.set('IN_PROGRESS', 'in-progress');
    statusColorMap.set('PENDING', 'not-started');
    statusColorMap.set('FAILED', 'failed');
    const statusCodeMap = new Map();
    statusCodeMap.set('COMPLETED', 'Completed');
    statusCodeMap.set('IN_PROGRESS', 'In Progress');
    statusCodeMap.set('PENDING', 'Pending');
    statusCodeMap.set('FAILED', 'Failed');

    $(document.body).tooltip({
        selector: "[title]",
        trigger: "click",
        placement: "auto bottom",
        html: "true"
    });

    $(document).on('click', '.retry', function() {
        let id = $(this).attr("id");
        const arr = id.split("::");
        let workflowId = arr[0];
        let taskId = arr[1];
        data = {
            workflowId: workflowId,
            taskId: taskId
        }
        $.ajax({
            type: "POST",
            url: "/retryTask",
            data: JSON.stringify(data),
            contentType: 'application/json',
            encode: true,
            success: function () {
                generateWF(); 
            },
            error: function (x, e) {
                console.log(e);
            }
        });
    });

    function generateWF() {
        $("#workflow-diagram").empty();
        $("#workflow-status").empty();
        $('#loader').show();
        const workflowId = $("#workflowId").attr("data-workflowId");
        url = `/workflowStatus/${workflowId}`;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (response) {
                $("#workflow-status").append(`<h3>${response.name}</h3>`);
                $("#workflow-status").append(`<h4>${statusCodeMap.get(response.workflowStatus)}</h4>`);
                let k = response.taskInstanceList.length - 1;
                var html = [];
                response.taskInstanceList.sort((a, b) => a.order - b.order);
                for (task of response.taskInstanceList) {
                    html.push('<div class="task">');
                    const task_status_color = statusColorMap.get(task.status);
                    let title = `Task: ${task.taskName}<br/>Status: ${statusCodeMap.get(task.status)}`;
                    if (task.url != null && task.status == 'IN_PROGRESS') {
                        let finalUrl = task.url;
                        if (finalUrl.includes("{{HOST}}")) {
                            finalUrl = task.url.replace("{{HOST}}", window.location.hostname);
                        }
                        else if (!finalUrl.includes("http")) {
                            finalUrl = window.location.protocol + "//" + window.location.hostname + ":30001" + task.url;
                        }
                        title = title + `<br/><a target='_blank' href='${finalUrl}'>Pending Form</a>`;

                    }
                    if (task.status == 'FAILED') {
                        if(task.statusMessage) {
                            title = title + `<br/><span>Reason: ${task.statusMessage}</span>`;
                        }
                        title = title + `<br/><span class='retry btn btn-outline-primary' id='${workflowId}::${task.taskId}'>Retry</span>`;
                    }
                    html.push(`<span class="task-circle color-${task_status_color}" data-toggle="tooltip" title="${title}"></span>`);
                    html.push(`<span class="task-name">${task.taskName}</span>`);
                    html.push('</div>');
                    if (k > 0) {
                        html.push(`<hr class="color-${task_status_color}"/>`);
                    }
                    k--;
                }
                $("#workflow-diagram").append(html.join(""));
                $('#loader').hide();

            },
            error: function (x, e) {
                console.log(e);
            }
        });
    }
    
    generateWF();
    setInterval(generateWF, 8000);
});