import csv
import os
import xml.etree.ElementTree as ET

result_dir = "./res"
apks_dir = "./apks"
check_list = "./checklist"
detail_dir = "./DetailedParse"
simple_dir = "./SimpleParse"

if __name__ == '__main__':
    detal_headers = ['name', 'target', 'fragments', 'dialogs', 'baseScreen']
    simple_headers = ['Pkg_Name', 'Activity_Nums', 'Fragment_Nums', "Dialog_Nums", "Screen_Nums", "Transition_Nums"]
    all_xml = []
    for xml in os.listdir(result_dir):
        # print(xml)
        all_xml.append(xml)
    simple_rows = []
    for xml in all_xml:
        xml_path = os.path.join(result_dir, xml)
        simple_dict = {'Pkg_Name': xml.split(".xml")[0]}
        act_nums = 0
        frg_nums = 0
        dia_nums = 0
        screen_nums = 0
        trans_nums = 0
        detal_rows = []
        fragment_sets = set()
        dialogs_sets = set()
        pairs_path = os.path.join("./pairs", xml.split(".xml")[0]+".txt")
        with open(pairs_path, "w") as f:
            pass

        if os.path.exists(xml_path):
            print("[+] Deal : ", xml_path)
            with open(xml_path, 'rt') as f:
                tree = ET.parse(f)
                # 逐个修个node
            # parse transitionEdges
            transitionNodeSet = ""
            for node in tree.iter():
                if node.tag == "transitionEdges":
                    transitionNodeSet = node
                    break
            for TransitionNode in transitionNodeSet.iter():
                if TransitionNode.tag == "TransitionEdge":
                    trans_nums = trans_nums + 1
                    pair = ""
                    src = ""
                    tgt = ""
                    for sonnode in TransitionNode.iter():
                        if sonnode.tag == "srcNode":
                            for s2n in sonnode.iter():
                                if s2n.tag == "name":
                                    src = s2n.text
                                    break
                        if sonnode.tag == "tgtNode":
                            for s2n in sonnode.iter():
                                if s2n.tag == "name":
                                    tgt = s2n.text
                                    break
                    pair = src + "->" + tgt + "\n"
                    print("[+] Find new pairs: ", pair)
                    with open(pairs_path, "a") as f:
                        f.writelines(pair)
            # parse screen node
            screenNodeSet = ""
            for node in tree.iter():
                if node.tag == "screenNodeSet":
                    screenNodeSet = node
                    break
            for ScreenNode in screenNodeSet.iter():
                if ScreenNode.tag == "ScreenNode":
                    new_dict = {}
                    screen_nums = screen_nums + 1
                    for info in ScreenNode.iter():
                        print(info.tag, info.text)
                        if info.tag == "name":
                            new_dict['name'] = info.text
                            try:
                                if "Activity" in info.text:
                                    act_nums = act_nums + 1
                            except:
                                pass
                        if info.tag == "target":
                            new_dict['target'] = info.text
                        if info.tag == "fragments":
                            flag = True
                            for sonnode in info.iter():
                                if sonnode.tag == "string":
                                    new_dict['fragments'] = sonnode.text
                                    fragment_sets.add(sonnode.text)
                                    flag = False
                            if flag:
                                new_dict['fragments'] = ""
                        if info.tag == "dialogs":
                            flag = True
                            for sonnode in info.iter():
                                if sonnode.tag == "string":
                                    new_dict['dialogs'] = sonnode.text
                                    dialogs_sets.add(sonnode.text)
                                    flag = False
                            if flag:
                                new_dict['dialogs'] = ""
                        if info.tag == "baseScreen":
                            new_dict['baseScreen'] = info.text
                    print(new_dict)
                    detal_rows.append(new_dict)
        print(detal_rows)
        detail_csv = os.path.join(detail_dir, xml.split(".xml")[0] + ".csv")
        with open(detail_csv, 'w') as f:
            f_csv = csv.DictWriter(f, detal_headers)
            f_csv.writeheader()
            f_csv.writerows(detal_rows)
        simple_dict['Activity_Nums'] = screen_nums
        simple_dict['Fragment_Nums'] = len(fragment_sets)
        simple_dict['Dialog_Nums'] = len(dialogs_sets)
        simple_dict['Screen_Nums'] = screen_nums
        simple_dict['Transition_Nums'] = trans_nums
        simple_rows.append(simple_dict)
    print(simple_rows)
    with open('./total.csv', 'w') as f:
        f_csv = csv.DictWriter(f, simple_headers)
        f_csv.writeheader()
        f_csv.writerows(simple_rows)
