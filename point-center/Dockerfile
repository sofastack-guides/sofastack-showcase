FROM registry.cn-shanghai.aliyuncs.com/sofastackcafe/sofaboot-runtime:2.0_java8_monitor_fix1_dstagent_4
# 载入修改的sofaboot入口脚本
COPY sofaboot-entry.sh /home/admin/scripts/sofaboot-entry.sh
# 装载包
COPY target/*-executable.jar /home/admin/release/run/
# 创建并赋予/home/admin/logs权限
RUN mkdir -p /home/admin/logs && \
  chown admin:admin /home/admin/logs
# 赋予sofaboot-entry权限
RUN chmod 755 /home/admin/scripts/sofaboot-entry.sh
# 通过自定义脚本启动应用
CMD ["/bin/bash" , "-c" , "/home/admin/scripts/sofaboot-entry.sh"]