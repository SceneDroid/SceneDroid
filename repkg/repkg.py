import os
import json
import subprocess
from treelib import Tree, Node
import xml.etree.ElementTree as ET
from xml.etree.ElementTree import ElementTree, Element
import hashlib


# 签名
def sign(project):
    # create key
    '''
    key_dir = os.path.join(project.unpack_path, "watson.keystore")
    cmd = "keytool -genkey -alias watson.keystore -keyalg RSA -validity 40000 -keystore " + key_dir
    print("[#] sign cmd: ", cmd)
    sign_result = subprocess.check_output(cmd, shell=True)
    if not os.path.exists(key_dir):
        print("[-] create key fault: ", project.p_id)
        exit(0)
    else:
        print("[+] create key success: ", project.p_id)
    '''
    # sign key
    key_dir = os.path.join(os.getcwd(), "watson.keystore")
    #print(project.apk_path)
    #print(project.apks_folder)
    pkg_name = project.apk_path.split(project.apks_folder+'/')[1]
    #print(pkg_name)
    #print(type(pkg_name))
    if not os.path.exists(key_dir):
        print("[-] find key fault: ", pkg_name)
        exit(0)
    else:
        print("[+] find key success: ", pkg_name)

    repkg_dir = os.path.join(project.unpack_path, "dist")
    print(repkg_dir)
    repkg_dir = os.path.join(repkg_dir, pkg_name)
    print(project.unpack_path)
    print(repkg_dir)
    if not os.path.exists(repkg_dir):
        print("[-] find repkg fault: ", pkg_name)
        exit(0)
    else:
        print("[+] find repkg success: ", pkg_name)

    cmd = "jarsigner -verbose -keystore " + " watson.keystore "
    sign_apk_name = pkg_name.split('.apk')[0] + "_sign.apk"
    sign_apk_dir = os.path.join(project.unpack_path, "dist", sign_apk_name)
    print("[#] sign apk name: ", sign_apk_name)
    cmd = cmd + "-signedjar " + sign_apk_dir + " " + repkg_dir + " " + " watson.keystore "
    cmd1 = "echo 'sigh987yu' | {0} ".format(cmd)

    print("[#] sign cmd: ", cmd1)

    sign_result = subprocess.check_output(cmd1, shell=True)
    # print(sign_result)
    if not os.path.exists(sign_apk_dir):
        print("[-] find sign apk fault: ", sign_apk_name)
        exit(0)
    else:
        print("[+] find sign apk success: ", sign_apk_name)



    # 对齐优化
    align_name = pkg_name.split('.apk')[0] + ".apk"
    align_dir = os.path.join(project.unpack_path, "dist", align_name)
    os.remove(align_dir)
    if not os.path.exists(align_dir):
        print("[#] align apk name: ", align_name)
        cmd = "zipalign -p -f 4 " + sign_apk_dir + " " + align_dir
        align_result = subprocess.check_output(cmd, shell=True)
        if not os.path.exists(align_dir):
            print("[-] find align apk fault: ", align_name)
            exit(0)
        else:
            print("[+] find align apk success: ", align_name)
    else:
        pass




    project.apk_path = align_dir
    project.align_name = align_name
    project.apk_dir = os.path.join(project.unpack_path, "dist")
    print("[+] All PKG repkg work kill!")


# 重打包
def repkg(project):
    cmd = "apktool b " + project.unpack_path + " --use-aapt2"
    print("[#] repkg cmd: ", cmd)
    repkg_result = subprocess.check_output(cmd, shell=True)
    dist_dir = os.path.join(project.unpack_path, "dist")
    if not os.path.exists(dist_dir):
        print("[-] repkg fault: ", project.p_id)
        exit(0)
    else:
        print("[+] repkg success: ", project.p_id)


def indent(elem, level=0):
    i = "\n" + level * "\t"
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = i + "\t"
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
        for elem in elem:
            indent(elem, level + 1)
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i


# 预处理
def pretreat(project):
    """
    :param project: 项目结构数据
    :return:
    """
    print("========== Repack apk file of '%s.apk' ==========" % project.p_id)
    manifestPath = os.path.join(project.unpack_path, "AndroidManifest.xml")
    print("[+] manifestPath: ", manifestPath)
    # 给每个Activity添加exproted参数和自定义intent-filter
    # 防止错误解析
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    # 读取Manifest文件
    with open(manifestPath, 'rt') as f:
        tree = ET.parse(f)
    # 逐个修个node
    for node in tree.iter():
        if "activity" in node.tag:
            print("[+] Find a Activity Node!")
            if "{http://schemas.android.com/apk/res/android}exported" in node.attrib:
                print("[+] This act have attr exported")
                node.attrib['{http://schemas.android.com/apk/res/android}exported'] = "true"
            else:
                print("[-] This act don't have attr exported")
                node.attrib['{http://schemas.android.com/apk/res/android}exported'] = "true"
            flag = True
            # 判断我们插入的intent filter是否已存在
            for child in node.iter():
                if child.tag == 'intent-filter':
                    for item in child.iter():
                        if item.tag == "action":
                            if item.attrib['{http://schemas.android.com/apk/res/android}name'] == 'zxy':
                                flag = False
            if flag:
                print("[+] this activity not add action")
                element = Element('intent-filter')
                action = Element('action', {'android:name': 'zxy'})
                category = Element('category', {'android:name': 'zxy'})
                element.append(action)  # 将二级目录加到一级目录里
                element.append(category)
                node.append(element)
            else:
                print("[+] this activity add action")
    root = tree.getroot()
    indent(root)
    # 重新写回xml
    tree.write(manifestPath, encoding='utf-8', xml_declaration=True)


def main(project):
    align_name = project.used_name.split('.apk')[0] + "_aligned.apk"
    if os.path.exists(os.path.join(project.unpack_path, "dist", align_name)):
        return
    # 预处理过程
    pretreat(project)
    # 重打包过程
    repkg(project)
    # 签名过程
    sign(project)


if __name__ == '__main__':
    testfile = "../testfile/AndroidManifest.xml"
    '''
    root = ET.parse(testfile).getroot()
    # 获取 XML 文档对象的根结点 Element
    # 递归查找所有的 neighbor 子结点
    
    for child in root:
        print(child.tag, child.attrib)
    
    for node in root.iter():
        print(type(node), node.tag, node.attrib)
    '''
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    with open(testfile, 'rt') as f:
        tree = ET.parse(f)
    for node in tree.iter():
        if node.tag == "activity":
            # print(node.attrib)
            print("[+] Find a Activity Node!")
            flag = False
            if "{http://schemas.android.com/apk/res/android}exported" in node.attrib:
                print("[+] This act have attr exported")
                node.attrib['{http://schemas.android.com/apk/res/android}exported'] = "true"
            else:
                print("[-] This act don't have attr exported")
                node.attrib['{http://schemas.android.com/apk/res/android}exported'] = "true"
            # print(node.tag, node.attrib)
            # print(type(node.attrib))
            '''
            for key in node.attrib:
                print(key)
                element = Element('intent-filter')
                action = Element('action', {'android:name': 'syc'})
                element.append(action)  # 将二级目录加到一级目录里
                node.append(element)
            '''
            flag = True
            for child in node.iter():
                if child.tag == 'intent-filter':
                    for item in child.iter():
                        if item.tag == "action":
                            if item.attrib['{http://schemas.android.com/apk/res/android}name'] == 'syc':
                                flag = False
            if flag:
                print("[+] this activity not add action")
                element = Element('intent-filter')
                action = Element('action', {'android:name': 'syc'})
                element.append(action)  # 将二级目录加到一级目录里
                node.append(element)
            else:
                print("[+] this activity add action")
    tree.write(testfile, encoding='utf-8', xml_declaration=True)
