__author__ = 'quynhdo'
#!/usr/local/bin/python

import os
import sys
import xml.etree.ElementTree as ET
from gurobipy import *
from distutils.dir_util import mkpath
xmls = os.popen("find . -name '*.xml'").readlines()
xmls = [x.strip() for x in xmls]

dtrRelations = ["BEFORE","OVERLAP","AFTER","BEFORE/OVERLAP"]
relations = ["BEFORE","OVERLAP","BEGINS-ON","ENDS-ON","CONTAINS","-NONE-","BEFORE-1","OVERLAP-1","BEGINS-ON-1","ENDS-ON-1","CONTAINS-1"]


for file in xmls:
	tree = ET.parse(file)
	data = tree.getroot()
	annotations = data[2]
	events = {}
	entities = []
	eventIds = []
	tlinks = {}
	for e in annotations.iter('entity'):
		id = e.find('id').text
		entities.append(id)
		type = e.find('type').text
		if(type=='EVENT'):
			eventIds.append(id)
			prop = e.find('properties')
			dtr  = prop.find('DocTimeRel').text
			dtrs = dtr.split("::")
			dtr = {}
			for x in dtrs:
				dtr_type, prob = x.split(":")
				prob=float(prob)
				dtr[dtr_type]=prob
			for dtr_type in dtrRelations:
				events[id, dtr_type]=dtr.get(dtr_type)
	for r in annotations.iter('relation'):
		id = r.find('id').text
		prop = r.find('properties')
		source = prop.find('Source').text
		type = prop.find('Type').text
		types = type.split("::")
		type = {}
		for x in types:
			tlink_type, prob = x.split(":")
			prob = float(prob)
			type[tlink_type] = prob
		target = prop.find('Target').text
		for tlink_type in relations:
			tlinks[id,tlink_type]=(source, target, type.get(tlink_type,0.0))

	x={}
	m = Model("tlink")
	linkRel = tuplelist()

	for (id,type), (source, target, prob) in tlinks.iteritems():
		x[source,target,type] = m.addVar(vtype=GRB.BINARY, obj=prob,name=','.join((source,target,type)))
		linkRel.append((source,target))

	y={}
	for (id, type), prob in events.iteritems():
		y[id, type] = m.addVar(vtype=GRB.BINARY, obj=prob, name = id+","+type)

	m.update()

	#add contraints for DocTimeRel all type's assignments add up to 1:
	for eid in eventIds:
		m.addConstr(quicksum(y[eid, r] for r in dtrRelations) == 1, "DocTimeRel,"+eid)


	#add constraints 1: for a pair of arguments, all types' assignments add up to 1:
	for source, target in linkRel:#.select('*','*'):
	        m.addConstr(quicksum(x[source,target,r] for r in relations) ==1, source+","+target )

	#add transitivity constraints for BEFORE and CONTAINS
	for source, media in linkRel:#.select('*','*'):
		for media, target in linkRel.select(media, '*'):
		    if((source,target) not in linkRel):
				linkRel.append((source,target))
				for r in relations:
					x[source,target,r]=m.addVar(vtype=GRB.BINARY, obj=1/11.0, name=source+','+target+","+r)
				m.update()
		    m.addConstr(x[source,media,'BEFORE']+x[media,target,'BEFORE']-x[source,target,'BEFORE']<=1, '%s_BEFORE_%s_BEFORE_%s' % (source,media,target))
		    m.addConstr(x[source,media,'BEFORE-1']+x[media,target,'BEFORE-1']-x[source,target,'BEFORE-1']<=1, '%s_BEFORE-1_%s_BEFORE-1_%s' % (source,media,target))
		    m.addConstr(x[source,media,'CONTAINS']+x[media,target,'CONTAINS']-x[source,target,'CONTAINS']<=1, '%s_CONTAINS_%s_CONTAINS_%s' % (source,media,target))
		    m.addConstr(x[source,media,'CONTAINS-1']+x[media,target,'CONTAINS-1']-x[source,target,'CONTAINS-1']<=1, '%s_CONTAINS-1_%s_CONTAINS-1_%s' % (source,media,target))

	#add DocTimeRel constraints for CONTAINS relations between two events, their docTimeRel values should be either the same or of types (OVERLAP, BEFORE/OVERLAP)
	for source, target in linkRel:#.select('*','*'):
	 	if((source in eventIds) and (target in eventIds)): #check if both argument are events
	 		m.addConstr(x[source,target,'CONTAINS']+x[source,target,'CONTAINS-1']+y[source,'BEFORE'] <= y[target,'BEFORE']+1, '%s_CONTAINS_%s_dtrBefore' % (source,target))
		#	m.addConstr(x[source,target,'CONTAINS']+x[source,target,'CONTAINS-1']+y[target,'BEFORE'] <= y[source,'BEFORE']+1, '%s_CONTAINS_%s_dtrBefore' % (source,target))

			m.addConstr(x[source,target,'CONTAINS']+x[source,target,'CONTAINS-1']+y[source,'AFTER'] <= y[target,'AFTER']+1, '%s_CONTAINS_%s_dtrAfter' % (source,target))

			m.addConstr(x[source,target,'CONTAINS']+y[target,'BEFORE/OVERLAP'] <= y[source,'BEFORE/OVERLAP']+1, '%s_CONTAINS_%s_dtrBO' % (source,target))
			m.addConstr(x[source,target,'CONTAINS-1']+y[source,'BEFORE/OVERLAP'] <= y[target,'BEFORE/OVERLAP']+1, '%s_CONTAINS_%s_dtr-1BO' % (source,target))
			m.addConstr(x[source,target,'CONTAINS']+y[target,'OVERLAP'] <= y[source,'BEFORE/OVERLAP']+y[source,'OVERLAP']+1, '%s_CONTAINS_%s_dtrOverlap' % (source,target))
			m.addConstr(x[source,target,'CONTAINS-1']+y[source,'OVERLAP'] <= y[target,'BEFORE/OVERLAP']+y[target,'OVERLAP']+1, '%s_CONTAINS_%s_dtrOverlap' % (source,target))


	m.modelSense = GRB.MAXIMIZE
	m.optimize()

	result={}
	for v in m.getVars():
		result[v.varName]=v.x

	#chenge the type of all entities:
	for e in annotations.findall('entity'):
		id = e.find('id').text
		type = e.find('type').text
		if(type=='EVENT'):
			dtr_type =""
			for r in dtrRelations:
				name = id+","+r
				if(result[name] == 1):
					dtr_type = r
					break
			prop = e.find('properties')
			dtr  = prop.find('DocTimeRel')
			dtr.text= dtr_type


	for r in annotations.findall('relation'):
		prop = r.find('properties')
		source = prop.find('Source').text
        	type = prop.find('Type')
        	target = prop.find('Target').text
		relation_type =""
		for rel in relations:
			if(result[",".join((source,target,rel))]==1):
				relation_type = rel
				break
		if(relation_type=="-NONE-"):
			print("*****delete relation\n")
			annotations.remove(r)
		elif(relation_type.endswith("-1")):
			print("*****find inverse relation "+relation_type)
			Source = prop.find('Source')
			Source.text = target
			Target = prop.find('Target')
			Target.text = source
			type.text = relation_type.split("-")[0]
		else:
			print("*****find proper relation: "+ relation_type)
			type.text = relation_type

	filename = file.split("/")[1]
	mkpath("GRB_processed/"+filename)
	tree.write("GRB_processed"+file.split(".")[1]+".xml")
