class usephone:
    def __init__(self, uiauto, dev_id):
        """
        :param uiauto: uiauto的可操作对象
        :param dev_id: adb的可操作对象名称
        """
        self.uiauto = uiauto
        self.dev_id = dev_id
