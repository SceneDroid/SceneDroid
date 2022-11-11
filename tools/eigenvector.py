import hashlib
import xml.etree.ElementTree as ET
from xml.etree.ElementTree import ElementTree, Element

def getVector(dxml, project):
    vector_str = ""
    child_stack = []
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    tree = ET.XML(dxml)
    '''
    with open("./tmp3.xml", 'rt') as f:
        tree = ET.parse(f)
    '''
    root = ""
    for node in tree.iter():
        if node.tag == "hierarchy":
            root = node
            break
    # print(root.attrib)
    for child in root:
        # print(child.tag, child.attrib)
        if child.attrib['package'] == project.used_name:
            child_stack.append(child)

    while len(child_stack) > 0:
        m = hashlib.md5()
        root = child_stack.pop()
        info = root.attrib
        if info['class'] != "android.widget.FrameLayout":
            tmpstr = info['resource-id'] + info['class'] + info['package']
            m.update(tmpstr.encode("utf8"))
            vector_str = vector_str + m.hexdigest()
        elif info['class'] == "android.widget.FrameLayout":
            if len(root) != 0:
            #if not root[0] == None and not root[0]:
                tmpstr = info['resource-id'] + info['class'] + info['package']
                m.update(tmpstr.encode("utf8"))
                vector_str = vector_str + m.hexdigest()
        if root.attrib['class'] == "android.widget.ListView":
            child_stack.append(root[0])
        else:
            for child in root:
                child_stack.append(child)
    vector = hashlib.md5()
    vector.update(vector_str.encode("utf8"))
    return vector.hexdigest()

if __name__ == '__main__':
    vector_str = ""
    child_stack = []
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    with open("./test.xml", 'rt') as f:
        tree = ET.parse(f)
    root = ""
    for node in tree.iter():
        if node.tag == "hierarchy":
            root = node
            break
    # print(root.attrib)
    for child in root:
        # print(child.tag, child.attrib)
        if child.attrib['package'] == "com.example.basicactivity_frg_2":
            child_stack.append(child)

    while len(child_stack) > 0:
        m = hashlib.md5()
        root = child_stack.pop()
        info = root.attrib
        if info['class'] != "android.widget.FrameLayout":
            tmpstr = info['resource-id'] + info['class'] + info['package']
            m.update(tmpstr.encode("utf8"))
            vector_str = vector_str + m.hexdigest()
        elif info['class'] == "android.widget.FrameLayout":
            if len(root) != 0:
                tmpstr = info['resource-id'] + info['class'] + info['package']
                m.update(tmpstr.encode("utf8"))
                vector_str = vector_str + m.hexdigest()
        if root.attrib['class'] == "android.widget.ListView":
            child_stack.append(root[0])
        else:
            for child in root:
                child_stack.append(child)
    vector = hashlib.md5()
    vector.update(vector_str.encode("utf8"))
    print(vector.hexdigest())