#!/usr/bin/env python
# -*- encoding: utf-8 -*-
from __future__ import print_function

import boto3
import json
import logging
import operator
from operator import itemgetter
from boto3.dynamodb.conditions import Key, Attr
from botocore.exceptions import ClientError

def scanAccounts():
	dynamodb = boto3.resource("dynamodb", region_name='ap-northeast-1', endpoint_url="https://dynamodb.ap-northeast-1.amazonaws.com")
	table = dynamodb.Table('Accounts')
	response = table.scan()
	return response

def updateRanking(listAccount):
	dynamodb = boto3.resource("dynamodb", region_name='ap-northeast-1', endpoint_url="https://dynamodb.ap-northeast-1.amazonaws.com")
	table = dynamodb.Table('Ranking')
	rank = 1;
	for i in listAccount:
		table.update_item(
	    	Key={
	        	'UserName': i['UserName']
	    	},
	    	UpdateExpression="set CurrentRank = :r",
	    	ExpressionAttributeValues={
	        	':r': rank
	    	},
	    	ReturnValues="UPDATED_NEW"
		)
		rank += 1
	return "UpdateItem succeeded:"

def lambda_handler(event, context):
    print(event)
    rs = scanAccounts()
    listAccount = rs['Items']
    newlistAccount = sorted(listAccount, key=itemgetter('Score'),reverse=True)
    update_rs = updateRanking(newlistAccount)
    print(update_rs)
