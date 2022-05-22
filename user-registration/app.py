#################################################
# Flask Python Microservice 
# Chelsea Fernandes
# Purpose: 	Makes startTask API accesible to Scheduler to begin task
#			Displays an Online Restaurant Order Form page
#			Processes Form Data to JSON format
#			Sends JSON data 
#			alerts Scheduler that task is complete via completeTask 
#			Routes user to submission confirmation
#################################################

from flask import Flask, request, redirect, Response, render_template, flash, url_for
from flask_bootstrap import Bootstrap
from flask import jsonify
from threading import Thread 
import time
import requests

############### global variables #################

app = Flask(__name__)
bootstrap = Bootstrap(app)

########################## APIs ###############################

# Expose startTask for scheduler to use
# use scheduler's completeTask API to send email back?

############### startTask API #################

#taskid, workflow and attributes from API body 
#attributes part of post request
@app.route('/startTask', methods=['POST','GET'])
def startTask():
	if request.method =='POST':
		# Thread(target = task).start()
		data = request.get_json()
		url = "/registration/" + str(data['workflowId']) + "/" + str(data['taskId'])
		#return url instead (reg)
		return {"url" : url}
	if request.method == 'GET':
		return 'No workflow data provided'
	return request.get_json()


############### completeTask #################

# make json and send to scheduler
def completeTask(item1, item2, item3, item4, workflowid, taskid):
	# calculate cost
	cost1 = int(item1) * 5
	cost2 = int(item2) * 7
	cost3 = int(item3) * 4
	cost4 = int(item4) * 3

	total = cost1+cost2+cost3+cost4

	data = {
		'status': 'COMPLETED',
		'workflowId': workflowid,
		'taskId': taskid,
		'attributes': {
			'Pizza Meal': item1 + '_' + str(cost1),
			'Hamburger Meal': item2 + '_' + str(cost2),
			'Hot Dog Meal': item3 + '_' + str(cost3),
			'Milkshake': item4 + '_' + str(cost4),
			'Total':  total
		}
	}
	return data


####################### The (NEW) Microservice Task #########################

# prints "hello python" in 1 second increments for 3 seconds.
@app.route("/registration/<workflowid>/<taskid>", methods=['POST','GET'])
def task(workflowid, taskid):
	# handle form, POST
	if request.method == 'POST': 
		if(request.form['qpizza']):
			item1 = request.form['qpizza']
		else:
			item1 = '0'
		if(request.form['qburger']):
			item2 = request.form['qburger']
		else:
			item2 = '0'
		if(request.form['qdog']):
			item3 = request.form['qdog']
		else:
			item3 = '0'
		if(request.form['qshake']):
			item4 = request.form['qshake']
		else:
			item4 = '0'
		data = completeTask(item1, item2, item3, item4, workflowid, taskid)
		
		######  TO DO: change the current url with the scheduler's completeTask API 
		requests.post("http://scheduler-service:8080/task/complete", json=data)

		return render_template('submission-confirmation.html', data=data)
	
	# if not submitting form, display form
	return render_template('email-form.html', workflowid=workflowid, taskid=taskid)


####################### The (OLD) Microservice Task #########################

# prints "hello python" in 1 second increments for 3 seconds.
def task_old():
	time.sleep(1)
	print("Hello Python 1")
	time.sleep(1)
	print("Hello Python 2")
	time.sleep(1)
	print("Hello Python 3")
	completeTask()
	return redirect("/completeTask", code=100)

@app.route("/")
def hello():
    return Response("Hi from your Flask app running in your Docker container!")

############### Start App #################

if __name__ == '__main__':
	app.run(host="0.0.0.0", port=8080)

