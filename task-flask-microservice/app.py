# Flask Python Microservice 
# Chelsea Fernandes

from flask import Flask, request, redirect, Response
from flask import jsonify
from threading import Thread 
import time

############### global variables #################

app = Flask(__name__)

########################## APIs ###############################

# Expose startTask and completeTask APIs for scheduler to use

############### startTask API #################
#taskid, workflow and attributes from API body 
#attributes part of post request
@app.route('/startTask', methods=['POST','GET'])
def startTask():
	if request.method =='POST':
		Thread(target = task).start()
		return '202 Accepted'
	if request.method == 'GET':
		return 'No workflow data provided'
	return '202 Accepted'


############### completeTask API #################
@app.route("/completeTask", methods=['POST', 'GET'])
def completeTask():
	print("Task Complete")
	return "202 Accepted"

####################### The Microservice Task #########################
# prints "hello python" in 1 second increments for 3 seconds.
def task():
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
	app.run(host="0.0.0.0", port=80)


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

