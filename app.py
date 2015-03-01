import os, json, time, redis
from flask import Flask, request, abort, render_template, Response
from flask.ext import restful
from flask.ext.restful import reqparse
from flask.ext.pymongo import PyMongo
from flask import make_response
from bson.json_util import dumps
from bson.objectid import ObjectId

from twilio.rest import TwilioRestClient


current_milli_time = lambda: int(round(time.time() * 1000))

MONGO_URL = os.getenv('MONGO_URL', 'mongodb://localhost:27017/buttonlight')
REDIS_URL = os.getenv('REDISTOGO_URL', 'redis://localhost:6379')
# red = redis.StrictRedis(socket_timeout=5).from_url(REDIS_URL)

TWILLIO_ACCOUNT_SID = os.getenv('TWILLIO_ACCOUNT_SID')
TWILLIO_AUTH_TOKEN = os.getenv('TWILLIO_AUTH_TOKEN')

app = Flask(__name__)

app.config['MONGO_URI'] = MONGO_URL
app.config['DEBUG'] = True
mongo = PyMongo(app)

def output_json(obj, code, headers=None):
    resp = make_response(dumps(obj), code)
    resp.headers.extend(headers or {})
    return resp

DEFAULT_REPRESENTATIONS = {'application/json': output_json}
api = restful.Api(app)
api.representations = DEFAULT_REPRESENTATIONS

class StatusList(restful.Resource):
    def __init__(self, *args, **kwargs):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('status', type=str)
        self.parser.add_argument('device_id', type=str)
        self.parser.add_argument('last', type=int)
        super(StatusList, self).__init__()

    def get(self):
        args = self.parser.parse_args()
        if args['device_id']:
            if args['last'] == 1:
                return [x for x in mongo.db.status.find({"device_id": args['device_id']}).sort("time", -1).limit(1) ]
            else:
                return [x for x in mongo.db.status.find({"device_id": args['device_id']})]
        else:
            if args['last'] == 1:
                raise NotImplementedError("find last time for each device_id ..")
            else:
                return [x for x in mongo.db.status.find()]

    def post(self):
        args = self.parser.parse_args()
        if not args['status'] or not args['device_id']: abort(400)
        status = int(args['status'])
        device_id = args['device_id']
        status_id =  mongo.db.status.insert({"time": current_milli_time(), "status": status, "device_id": device_id})
        obj = mongo.db.status.find_one({"_id": status_id})
        # red.publish('status', dumps(obj))
        return obj

class Status(restful.Resource):
    def get(self, status_id):
        return mongo.db.status.find_one_or_404({"_id": status_id})

    def delete(self, status_id):
        mongo.db.status.find_one_or_404({"_id": status_id})
        mongo.db.status.remove({"_id": status_id})
        return '', 204

# def event_stream():
#     # yield 'retry: 10000\n'
#     pubsub = red.pubsub()
#     pubsub.subscribe('status')
#     for message in pubsub.listen():
#         print message
#         yield 'data: %s\n\n' % message['data']
#     pubsub.close()

# @app.route('/stream')
# def stream():
#     return Response(event_stream(), mimetype="text/event-stream")

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/text', methods=['POST'])
def text():
    client = TwilioRestClient(TWILLIO_ACCOUNT_SID, TWILLIO_AUTH_TOKEN)
    message1 = client.messages.create(body="Light was reset.", to="+19739851417", from_="+19737551570")
    message2 = client.messages.create(body="Light was reset.", to="+18473024039", from_="+19737551570")
    return str(message1.sid + " " + message2.sid)

api.add_resource(StatusList, '/status/')
api.add_resource(Status, '/status/<ObjectId:status_id>')

if __name__ == '__main__':
	app.run(debug=True, host="0.0.0.0", port=int(os.environ.get("PORT", 5000)))
