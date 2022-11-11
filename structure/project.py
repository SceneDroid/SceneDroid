import os
from graphviz import Digraph
from skimage.metrics import structural_similarity as compare_ssim
from skimage import io

from fuzz import buildscreen
from structure import iccbot


class project:
    def __init__(self, p_id, res_dir, version, used_name, apk_path):
        """
        :param p_id: 单个APK的项目ID
        :param res_dir: 所处理的APK结果文件夹
        :param version: APK的版本信息
        :param used_name: APK使用的包名
        :param apk_path: APK所存储的路径
        """
        # 整个项目的ID # com.example.mynav-1
        self.p_id = p_id
        # 整个项目的整体工作目录
        self.res_dir = res_dir
        if not os.path.exists(self.res_dir):
            os.mkdir(self.res_dir)
        # parse_ic3 result
        self.parsed_ic3 = ""
        # 整个项目的APK版本号
        self.version = version
        # 整个项目所用的包名 # com.example.mynav
        self.used_name = used_name
        # apk包名 # app-debug.apk
        self.apk_name = ""
        # 整个项目的APK安装包所在路径 # /home/syc/Downloads/DEV/rebuild/result/com.example.mynav-1/unpack/dist/app-debug.apk
        self.apk_path = apk_path
        # 整个项目的APK解包结果路径
        self.unpack_path = ""
        # 整个项目Mainfest文件解析结果
        self.parseMain = {}
        # 整个项目所有截图的所在目录
        self.screenshot_dir = os.path.join(self.res_dir, "screenshot")
        if not os.path.exists(self.screenshot_dir):
            os.mkdir(self.screenshot_dir)
        # activity coverage数据保存
        self.actcoverage = set()
        # 整个项目存在的场景列表，以特征向量形式保存
        self.screenlist = set()
        # 整个项目存在的场景对象列表
        self.screenobject = []
        # apk_dir_path
        self.apk_dir = ""
        # 整个项目的STG图
        self.stg = ""
        # 临时文本文件
        self.tmptxt = os.path.join(self.res_dir, "tmp.txt")
        # 临时截图
        self.tmppng = os.path.join(self.res_dir, "tmp.png")
        # 整个项目的Acitivy列表
        self.activity = []
        # 转换关系trans
        self.inittrans = set()
        # 整个项目的Activity转换关系
        self.activitytrans = []
        # 整个项目的Screen转换关系
        self.screentrans = []
        # 整个项目的Pkg转换关系
        self.pkgtrans = []
        # act转换图
        self.atg_dog = Digraph(comment='Activity Transition Graph')
        # 输出名称
        self.atg_gv = os.path.join(res_dir, 'atg.gv')
        # sct转换图
        self.stg_dog = Digraph(comment='Screen Transition Graph')
        self.stg_gv = os.path.join(res_dir, 'stg.gv')
        # pkg转换图
        self.pkg_dog = Digraph(comment='PKG Transition Graph')
        self.pkg_gv = os.path.join(res_dir, 'pkg.gv')
        # iccbot转换图
        self.iccbot_dog = Digraph(comment='ICCBOT Transition Graph')
        self.iccbot_gv = os.path.join(res_dir, 'ICCBOT.gv')
        # Total转换图
        self.total_dog = Digraph(comment='Total Transition Graph')
        self.total_gv = os.path.join(res_dir, 'total.gv')
        # 对应的布局XML保存地址
        self.layout_dir = os.path.join(self.res_dir, "layout")
        if not os.path.exists(self.layout_dir):
            os.mkdir(self.layout_dir)
        # 模糊测试的日志
        self.fuzzlog = os.path.join(self.res_dir, "fuzzlog.txt")
        # soot目录
        self.sootOutput_dir = ""
        # self activity num
        self.actnum = 0
        self.apks_folder = ""
        self.root_dir = ""
        self.align_name = ""
        self.act_paras_file = ""
        self.static_enhance = ""
        # iccbot
        self.iccobj = ""
        self.icc_res = os.path.join(self.res_dir, "iccbot")
        if not os.path.exists(self.icc_res):
            os.mkdir(self.icc_res)
        self.entrances = []
        self.jadx_res = os.path.join(self.res_dir, "jadx")
        if not os.path.exists(self.jadx_res):
            os.mkdir(self.jadx_res)
        self.rjava_res = ""
        # 持久化目录
        self.storge = os.path.join(self.res_dir, "entry")
        if not os.path.exists(self.storge):
            os.mkdir(self.storge)
        self.startActCmd = os.path.join(self.res_dir, "StartCMD.txt")
        if not os.path.exists(self.startActCmd):
            with open(self.startActCmd, "w") as f:
                f.writelines("")
        self.startActCmdRes = os.path.join(self.res_dir, "StartCMDResult.txt")
        if not os.path.exists(self.startActCmdRes):
            with open(self.startActCmdRes, "w") as f:
                f.writelines("")

        self.successact = os.path.join(self.res_dir, "succeACT.txt")
        if not os.path.exists(self.successact):
            with open(self.successact, "w") as f:
                f.writelines("")
        self.actScreen = os.path.join(self.res_dir, "actScreen.txt")
        if not os.path.exists(self.actScreen):
            with open(self.actScreen, "w") as f:
                f.writelines("")
        self.NoneactScreen = os.path.join(self.res_dir, "NoneactScreen.txt")
        if not os.path.exists(self.NoneactScreen):
            with open(self.NoneactScreen, "w") as f:
                f.writelines("")
        self.transitionScreen = os.path.join(self.res_dir, "transitionScreen.txt")
        if not os.path.exists(self.transitionScreen):
            with open(self.transitionScreen, "w") as f:
                f.writelines("")

        self.actScreenlist = set()
        self.NoneactScreenlist = set()
        self.totalstep = 0

        #
        self.SecondStart = os.path.join(self.res_dir, "SecondStart.txt")
        if not os.path.exists(self.SecondStart):
            with open(self.SecondStart, "w") as f:
                f.writelines("")

    def setAct(self, actlist):
        self.activity = actlist

    # 设置项目的Apktools解APK包后的结果路径
    def setUnpack(self, path):
        """
        :param path: 设置Apktools解APK包后的结果路径
        :return:
        """
        self.unpack_path = path

    # 设置项目的Mainfest文件信息路径
    def setParse(self, parseMain):
        """
        :param parseMain: 解析出Mainfest文件的路径
        :return:
        """
        self.parseMain = parseMain

    # 设置项目的STG图
    def setStg(self, stg):
        self.stg = stg

    # 打印项目的所有信息
    def printAll(self):
        print("###################################")
        print("[~]p_id: ", self.p_id)
        print("[~]res_dir: ", self.res_dir)
        print("[~]version: ", self.version)
        print("[~]apk_name: ", self.apk_name)
        print("[~]used_name: ", self.used_name)
        print("[~]apk_path: ", self.apk_path)
        print("[~]unpack_path: ", self.unpack_path)
        if self.icc_res != "":
            print("[~]icc result: ", self.icc_res)
        print("[~]activity: ", self.activity)
        print("[~]screenlist: ", self.screenlist)
        print("###################################")

    def isAliveScreen(self, vector, command, act, startact, parentsc, dshot):
        """
        :param vector: 新的场景特征向量值
        :param command: 新的场景组件操作路径信息
        :param act: 新的场景所属的Activity
        :param startact: 新的场景启动的所属的Activity
        :param parentsc: 新的场景父Screen节点
        :param dshot: 新的场景截图
        :return: 是否为新的特征向量
        """
        for v in self.screenlist:
            # 检查Vector
            if v == vector:
                print("[-] This Screen is alive!")
                print("[V] : ", vector)
                for obj in self.screenobject:
                    if obj.vector == vector:
                        print("[obj vector]: ", obj.vector)
                        print("[obj command]: ", obj.command)
                        print("[command]: ", command)
                        if len(obj.command) > len(command):
                            obj.command = command
                            obj.act = act
                            obj.start = startact
                            obj.parentScreen = parentsc
                            print("[+] Find a new Screen Path!")
                            #buildscreen.init(obj, self)
                            return False
                    else:
                        continue
                print("[-] This Screen is alive!")
                return False

        '''
        # 检查Picture
        for obj in self.screenobject:
            img1 = io.imread(dshot)
            img2 = io.imread(obj.shot)
            ssim1 = compare_ssim(img1, img2, multichannel=True)
            print("[ssim1]-> ", obj.vector, " : ", ssim1)
            if ssim1 >= 0.999:
                print("[-] This Screen is alive!")
                print("[V] : ", obj.vector)
                return False'''

        print("[-] This Screen is New: ", vector)
        return True

    def printscreen(self):
        for screen in self.screenobject:
            screen.printAll()
            buildscreen.init(screen, self)

    def printTrans(self):
        print("========== Project Trans ==========")
        scrtxt = os.path.join(self.res_dir, "screentrans.txt")
        with open(scrtxt, 'w') as f:
            pass
        pkgxt = os.path.join(self.res_dir, "pkgtrans.txt")
        with open(pkgxt, 'w') as f:
            pass
        totaltrans = os.path.join(self.res_dir, "trans.txt")
        with open(totaltrans, 'w') as f:
            pass
        print("[Total]")
        for trans in self.inittrans:
            with open(totaltrans, 'a') as f:
                f.writelines(trans + "\n")
            print(trans)
        print("[Screen]")
        for sce in self.screentrans:
            with open(scrtxt, 'a') as f:
                f.writelines(sce + "\n")
            print(sce)
        print("[PKG]")
        for pkg in self.pkgtrans:
            with open(pkgxt, 'a') as f:
                f.writelines(pkg + "\n")
            print(pkg)

    # 保存转换关系图
    def savegv(self):
        try:
            for trans in self.inittrans:
                father = trans.split('->')[0]
                son = trans.split('->')[-1]
                self.total_dog.node(father, father)
                self.total_dog.node(son, son)
                self.total_dog.edge(father, son)
            self.total_dog.render(self.total_gv, view=True)
        except:
            pass

        try:
            with open(os.path.join(self.res_dir, "iccbot.txt"), "r") as f:
                for line in f.readlines():
                    father = line.split('->')[0]
                    son = line.split('->')[-1]
                    self.iccbot_dog.node(father, father)
                    self.iccbot_dog.node(son, son)
                    self.iccbot_dog.edge(father, son)
            self.iccbot_dog.render(self.iccbot_gv, view=True)
        except:
            pass
        '''
        project.stg_dog.node(screen.vector, screen.vector)
        except:
        pass

        try:
        project.stg_dog.node(screenvector, screenvector)
        project.stg_dog.edge(screen.vector, screenvector)
        

        self.atg_dog.render(self.atg_gv, view=True)
        self.stg_dog.render(self.stg_gv, view=True)
        self.pkg_dog.render(self.pkg_gv, view=True)'''


    def initicc(self):
        print("[#] Start init ICC OBJ")
        try:
            print("[icc_res] : ", self.icc_res)
            self.iccobj = iccbot.iccbotres(self.icc_res)
            print("[-] Successfull init ICC OBJ")
        except:
            print("[-] Fail init ICC OBJ")