import os.path
import json
from treelib import Tree, Node
import xml.etree.ElementTree as ET

test_dir = "/Users/syc/AndroidStudioProjects/MyApplication/app/build/outputs/apk/debug/res"
test_xml_dir = '/Users/syc/AndroidStudioProjects/MyApplication/app/build/outputs/apk/debug/res/res/layout/activity_main.xml'
test_xml2_dir = '/Users/syc/AndroidStudioProjects/MyApplication/app/build/outputs/apk/debug/test.xml'
same_widget = []
test_wig = {'bounds': {'bottom': 628, 'left': 147, 'right': 467, 'top': 560},
            'childCount': 0, 'className': 'android.widget.EditText',
            'contentDescription': '', 'packageName': 'com.example.myapplication',
            'resourceName': None, 'text': '默认提示文本',
            'visibleBounds': {'bottom': 628, 'left': 147, 'right': 467, 'top': 560},
            'checkable': False, 'checked': False, 'clickable': True,
            'enabled': True, 'focusable': True, 'focused': False,
            'longClickable': True, 'scrollable': False, 'selected': False}


def findres_id(wid_info, layout):
    bounds = wid_info['bounds']
    bounds_str = "["
    tmp_x = []
    tmp_y = []
    bounds_str = bounds_str + str(bounds['left']) + ","
    bounds_str = bounds_str + str(bounds['top']) + ']['
    bounds_str = bounds_str + str(bounds['right']) + ','
    bounds_str = bounds_str + str(bounds['bottom']) + ']'
    # print(bounds_str)
    tree = ET.parse(layout)
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    # 逐个修个node
    for node in tree.iter():
        # print("tag: ", node.tag)
        # print("attrib: ", node.attrib)
        try:
            if node.attrib['bounds'] == bounds_str:
                # print(node.attrib['resource-id'])
                return node.attrib['resource-id']
        except:
            pass


def checkfile(path, w_class, w_res_id=""):
    global same_widget
    # 读取Manifest文件
    with open(path, 'rt') as f:
        tree = ET.parse(f)
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    # 逐个修个node
    flag = False
    for node in tree.iter():
        # print("tag: ", node.tag)
        # print("attrib: ", node.attrib)
        for key in node.attrib:
            if key == '{http://schemas.android.com/apk/res/android}id':
                # print("attrib: ", node.attrib[key])
                if w_class == node.tag:
                    if w_res_id in node.attrib[key]:
                        flag = True
                        print("[+] Find same widget: ", node.attrib)
                        same_widget.append(node)


def get_filelist(wdir, w_class, w_res_id):
    global same_widget
    for home, dirs, files in os.walk(wdir):
        for filename in files:
            fullname = os.path.join(home, filename)
            if '.xml' in fullname:
                #print(fullname)
                checkfile(fullname, w_class, w_res_id)
    print("[+] same_widget: ", same_widget)
    same_widget = same_widget[0]
    same_inputType = same_widget.attrib["{http://schemas.android.com/apk/res/android}inputType"]
    same_hint = same_widget.attrib["{http://schemas.android.com/apk/res/android}hint"]
    print("[+] same_inputType: ", same_inputType)
    print("[+] same_hint: ", same_hint)
    res = []    # 0: inputType, hint
    res.append(same_inputType)
    res.append(same_hint)
    return res


# 寻找到解包中存在的更详细的组件信息
def find(project, wid_info, layout):
    res_layout_dir = os.path.join(project.unpack_path, "res")
    try:
        w_class = wid_info['className']
        if w_class == "":
            print("[-] Please input w_class!")
            print("[-] Don't Find same Widget info!")
            return []
        w_res_id = findres_id(wid_info, layout)
        if w_res_id == "":
            print("[-] Please input w_res_id!")
            print("[-] Don't Find same Widget info!")
            return []
        w_class = w_class.split('.')[-1]
        w_res_id = w_res_id.split('/')[-1]
        print("[+] Start Find Widget: ", w_class, w_res_id)
        res = get_filelist(res_layout_dir, w_class, w_res_id)
        if res:
            print("[+] res: ", res)
            return res
        else:
            print("[-] Don't Find same Widget info!")
            return []
    except:
        print("[-] Don't Find same Widget info!")
        return []


'''
if __name__ == '__main__':
    bounds = test_wig['bounds']
    bounds_str = "["
    tmp_x = []
    tmp_y = []
    bounds_str = bounds_str + str(bounds['left']) + ","
    bounds_str = bounds_str + str(bounds['top']) + ']['
    bounds_str = bounds_str + str(bounds['right']) + ','
    bounds_str = bounds_str + str(bounds['bottom']) + ']'
    print(bounds_str)
    dir_list = os.path.join(test_dir, "res")
    print(dir_list)
    # get_filelist(dir_list, test_wig[]
    with open(test_xml2_dir, 'rt') as f:
        tree = ET.parse(f)
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    # 逐个修个node
    for node in tree.iter():
        # print("tag: ", node.tag)
        # print("attrib: ", node.attrib)
        try:
            if node.attrib['bounds'] == bounds_str:
                print(node.attrib['resource-id'])
        except:
            pass
    classa = "android.widget.EditText"
    print(classa.split('.')[-1])
    classa = classa.split('.')[-1]
    resource_id = "com.example.myapplication:id/editTextPhone"
    resource_id = resource_id.split('/')[-1]
    print(resource_id)

    get_filelist(dir_list, w_class=classa, w_res_id=resource_id)

    print(same_widget)
    print(same_widget[0].attrib)
'''
