import os
import json
import subprocess
from treelib import Tree, Node
import xml.etree.ElementTree as ET
from xml.etree.ElementTree import ElementTree, Element
import hashlib

# Get the parameters to start the activity
def extract_activity_action(manifestPath, project):
    # {activity1: {actions: action1, category: cate1}}
    # new format: {activity: [[action1, category1],[action2, category2]]}
    actnum = 0
    d = {}
    #ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    # 读取Manifest文件
    with open(manifestPath, 'rt') as f:
        tree = ET.parse(f)
        # 逐个修个node
    for node in tree.iter():
        print(node.tag, node.attrib)
        if node.tag == "component" and node.attrib['type'] == "Activity" :
            actnum = actnum + 1
            d[node.attrib['name']] = []
            print(node.tag, node.attrib)
            for child1 in node.iter():
                if child1.tag == "intent_filter":
                    action_category_pair = ["", ""]
                    if 'action' in child1.attrib:
                        if child1.attrib['action']:
                            action_category_pair[0] = child1.attrib['action']
                    if 'category' in child1.attrib:
                        if child1.attrib['category']:
                            action_category_pair[1] = child1.attrib['category']
                    # action_category_pair = [child1.attrib['action'], child1.attrib['category']]
                    d[node.attrib['name']].append(action_category_pair)
    project.actnum = actnum
    return d


def parseManifest(p):
    print("========== Parsing manifest file of '%s.apk' ==========" % p.p_id)
    if not os.path.exists(p.unpack_path):
        print("[-] cannot find the decompiled app: " + p.p_id)
        return
    else:
        print("[+] find the decompiled app: " + p.p_id)
    manifestPath = os.path.join(p.iccobj.ctg, "componentInfo.xml")
    print("[+] manifestPath: ", manifestPath)
    pairs = extract_activity_action(manifestPath, p)
    print(pairs)
    # format of pairs: {activity1: {actions: action1, category: cate1 }} -----discard
    # new format: {activity: [[action1, category1],[action2, category2]]}
    ##get all activity and their attributes
    return pairs

'''
if __name__ == '__main__':
    test = "com.gaurav.avnc"
    path = "../testfile/AndroidManifest.xml"
    parse_result = extract_activity_action(path, test)
    print(parse_result)
    # 初始化Activiy列表
    actlist = []
    for act in parse_result:
        if act.split(test)[1] not in actlist:
            actlist.append(act.split(test)[1])
    print(actlist)
'''