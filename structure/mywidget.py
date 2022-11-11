# 组件数据结构
class mywidget:
    def __init__(self, ui2):
        """
        :param ui2: 从uiauto里获得的组件对象
        """
        self.ui2 = ui2
        self.nextscreen = ""
        self.nextpkg = ""
        self.nextact = ""

    def updateScreen(self, screenID):
        """
        :param screenID: 若点击这个widget可以启动的Screen
        :return:
        """
        self.nextscreen = screenID

    def updatePkg(self, PkgName):
        """
        :param PkgName: 若点击这个widget可以启动的新程序包
        :return:
        """
        self.nextpkg = PkgName

    def updateAct(self, ActName):
        """
        :param ActName: 若点击这个widget可以启动的新Activity
        :return:
        """
        self.nextact = ActName
