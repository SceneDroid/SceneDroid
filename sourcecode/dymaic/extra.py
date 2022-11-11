import os
import json

def convert(type, name):
    extras = ''
    if 'String' in type:
        extras = extras + ' --es ' + name + ' test'
    if 'Int' in type:
        extras = extras + ' --ei ' + name + ' 1'
    if 'Bool' in type:
        extras = extras + ' --ez ' + name + ' False'
    if 'Float' in type:
        extras = extras + ' --ef ' + name + ' 0.1'
    if 'Long' in type:
        extras = extras + ' --el ' + name + ' 1'
    return extras


def get_extra_paras(project, activity):
    component_dir = os.path.join(project.iccobj.iccsep, "ComponentModel.json")
    extras = []
    if os.path.exists(component_dir):
        print("[+] Find component json")
    else:
        return extras
    with open(component_dir, 'r') as f:
        data = json.load(f)
    components = data['components']
    for component in components:
        #print(component)
        if component['className'] == activity:
            recvIntents = component['fullValueSet']['extras']['recvIntent']
            for recv in recvIntents:
                name = recv['name']
                type = recv['type']
                extra = convert(type, name)
                extras.append(extra)
    return extras

if __name__ == '__main__':
    extras = []
    component_dir = os.path.join("/home/syc/Downloads/DEV/rebuild/result/a2dp.Vol-169/iccbot/a2dp.Vol_169/ICCSpecification/ComponentModel.json")
    with open(component_dir, 'r') as f:
        data = json.load(f)
    #print(data)
    class_name = "a2dp.Vol.LocViewer"
    components = data['components']
    #print(components)
    for component in components:
        #print(component)
        if component['className'] == class_name:
            recvIntents = component['fullValueSet']['extras']['recvIntent']
            for recv in recvIntents:
                name = recv['name']
                type = recv['type']
                extra = convert(type, name)
                extras.append(extra)
    print(extras)
