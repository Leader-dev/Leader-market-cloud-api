//
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖保佑             永无BUG
//
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
//
//			我向佛祖许愿：
//					项目就快上线了，我向佛祖许愿
//					佛说：我可以让你许一个愿
//					我对佛说，那就让我的代码永远不出BUG
//					佛说：只能四天
//					我说：行。春天，夏天，秋天，冬天
//					佛说，不行，只能三天
//					我说，那就昨天，今天，明天。
//					佛说：不行，两天
//					我说那就白天，黑天。
//					佛说，只能一天
//					我说行
//					佛茫然地看着我说：哪一天
//					我说：每一天
//					佛哭了，说：就让你的代码，永远不出BUG
//					希望看到这条注释的朋友，请你们拷贝一下
//					如果有自己的项目的话，请你们粘贴到入口文件的开头
//					祝所有的朋友，永远不出BUG，开开心心
//

package com.leader.marketcloudapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class MarketCloudApiApplication

fun main(args: Array<String>) {
	runApplication<MarketCloudApiApplication>(*args)
}
