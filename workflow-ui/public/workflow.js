$(function () {
    const statusColorMap = new Map();
    statusColorMap.set('COMPLETED', 'complete');
    statusColorMap.set('IN_PROGRESS', 'in-progress');
    statusColorMap.set('PENDING', 'not-started');
    const statusCodeMap = new Map();
    statusCodeMap.set('COMPLETED', 'Completed');
    statusCodeMap.set('IN_PROGRESS', 'In Progress');
    statusCodeMap.set('PENDING', 'Pending');

  $(document.body).tooltip({
      selector: "[title]",
      trigger: "click",
      placement: "auto bottom",
      html: "true"
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
                  if(task.url != null && task.status == 'IN_PROGRESS') {
                      title = title + `<br/><a href='${task.url}'>Pending Form</a>`;
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