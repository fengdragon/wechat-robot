# wechat-robot

wechat-robot是基于微信网页版协议开发的普通微信号机器人程序，使用Java语言。

[微信协议分析](doc/protocol.md)

# 源码说明

webchat-robot 是基于 biezhi 的源码上进行改进，原 [repo](https://github.com/biezhi/wechat-robot)，原 [README.md](README.old.md)

## 新增功能
* 自动抓取导购网站的优惠信息，目前包括 smzdm.com（电器、图书类优惠），转发到指定微信群。
* 自动抓取登录账户接收到的滴滴优惠券、饿了么优惠券等（加入到的各种优惠券群），转发到指定微信群。
