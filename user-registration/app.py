# Flask Python Microservice 
# Chelsea Fernandes

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
		print(url)
		return {"url" : url}
	if request.method == 'GET':
		return 'No workflow data provided'
	return request.get_json()


############### completeTask #################

# make json and send to scheduler
def completeTask(email, workflowid, taskid):
	data = {
		'workflowId': workflowid,
		'task_id': taskid,
		'attributes': [{
			'name': 'email',
			'value': email 
		}]
	}
	return data


####################### The (NEW) Microservice Task #########################

# prints "hello python" in 1 second increments for 3 seconds.
@app.route("/registration/<workflowid>/<taskid>", methods=['POST','GET'])
def task(workflowid, taskid):
	# handle form, POST
	if request.method == 'POST': 
		email = request.form['email']
		data = completeTask(email, workflowid, taskid)
		print(data)
		
		# TO DO: change the current url with the scheduler's completeTask API 
		requests.post("https://82215c4b-8829-47de-80ae-5d0603e4b86e.mock.pstmn.io/completeTask", json=data)
		return render_template('submission-confirmation.html', email=email, data=data)
	# if not submitting form, display form
	return render_template('email-form.html', workflowid=workflowid, taskid=taskid)



# # prints "hello python" in 1 second increments for 3 seconds.
# @app.route("/registration/<workflowid>/<taskid>", methods=['GET'])
# def task(workflowid, taskid):
# 	return render_template('email-form.html', workflowid=workflowid, taskid=taskid)


# @app.route("/email", methods=['POST', 'GET'])
# def handle_form():
# 	if request.method == 'POST':
# 		email = request.form['email']
# 		#get workflowid and taskid
# 		completeTask()
# 		return render_template('submission-confirmation.html', email=email)
# 	return render_template('email-form.html')



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


################ old code ################
# expose startTask API to scheduler
# example url route: /startTask/123/attributes?attr1key=attr1val&attr2key=attr2val
#taskid, workflow and attributes from body 
#attributes part of post request
# @app.route('/startTask', methods=['POST','GET'])
# def startTask(workflowId, attributes):
# 	 # query = request.args.to_dict()
# 	 # query = jsonify(query)
# 	 # print(query)
# 	 if request.method =='GET':
# 	 	return "<h1>202 Accepted - Task Started = " + workflowId
# 	 return "202 Accepted"

	
# expose completeTask API to scheduler
# example url route: /completeTask/123/attributes?attr1key=attr1val&attr2key=attr2val
# @app.route('/completeTask/<workflowId>/<attributes>', methods=['GET'])
# def completeTask(workflowId, attributes):
# 	query = request.args.to_dict()
# 	print(query)
# 	if request.method =='GET':
# 		return "<h1>202 Accepted - Task Complete = " + workflowId
# 	return "202 Accepted"

