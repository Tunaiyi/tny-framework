package com.tny.game.suite.net.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.common.word.WordsFilter;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.command.plugins.filter.AbstractParamFilter;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.message.Message;
import com.tny.game.suite.net.filter.annotation.NameFilter;
import com.tny.game.suite.utils.SuiteResultCode;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME, TEXT_FILTER})
public class NameLimit<UID> extends AbstractParamFilter<UID, NameFilter, String> {

    @Resource
    private WordsFilter wordsFilter;

    protected NameLimit() {
        super(NameFilter.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message<UID> message, int index, NameFilter annotation, String param) {
        int size = param.length();
        if (size < annotation.lowLength() || annotation.highLength() < size) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    message.getUserID(), message.getProtocol(),
                    index, size, annotation.lowLength(), annotation.highLength());
            return SuiteResultCode.NAME_LENGTH_ILLEGAL;
        }
        if (this.wordsFilter.hasBadWords(param)) {
            return SuiteResultCode.NAME_FILTER_WORD;
        }
        return ResultCode.SUCCESS;
    }

//     public static void main(String[] args) {
//         String[] values = {
//                 "dafds",
//                 "jdklsfjlkf}dsa",
//                 "jdklsfjlkf{dsa",
//                 "jdklsfjlkf(dsa",
//                 "jdklsfjlkf)dsa",
//                 "jdklsfjlkf[dsa",
//                 "jdklsfjlkf]dsa",
//                 "jdklsfjlkf*dsa",
//                 "jdklsfjlkf?dsa",
//                 "jdklsfjlkf\"dsa",
//                 "jdklsfjlkf+dsa",
//                 "jdklsfjlkf'dsa",
//                 "jdklsfjlkf$dsa",
//                 "jdklsfjlk^dsa",
//                 "jdklsfjlkf\\dsa",
//         };
//         for (String v : values)
//             System.out.println(v + " : " + fullPattern.matcher(v).find());
//     }
// }
}
