package com.tny.game.net.endpoint;

import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public class CommonSessionTest extends NetEndpointTest<CommonSession<Long>> {

	@Override
	protected CommonSession<Long> newEndpoint(CommonSessionSetting setting) {
		return new CommonSession<>(setting, new EndpointContext<Long>() {

			@Override
			public CertificateFactory<Long> getCertificateFactory() {
				return new DefaultCertificateFactory<>();
			}

			@Override
			public MessageDispatcher getMessageDispatcher() {
				return null;
			}

			@Override
			public CommandTaskProcessor getCommandTaskProcessor() {
				return null;
			}
		});
	}

	@Override
	public void receive() {

	}

	@Override
	public void send() {

	}

	@Override
	public void resend() {

	}

	// @Test
	// public void closeParallel() {
	//     NetTunnel<Long> loginTunnel = mockTunnel(createLoginCert());
	//     CommonSession<Long> session = create(loginTunnel);
	//     TestAide.parallelTask(
	//             TestTask.runnableTask("closeParallelChangeStatue", 30, () -> {
	//                 session.offline();
	//                 try {
	//                     session.online(mockTunnel(createLoginCert(certificateId, uid)));
	//                 } catch (ValidatorFailException ignored) {
	//                 }
	//             }),
	//             TestTask.runnableTask("closeParallel", 3, () -> {
	//                 Thread.sleep(1);
	//                 session.close();
	//             }));
	//     assertEquals(1, session.getCloseTimes());
	// }
	//
	// protected static class CommonSession<Long> extends CommonSession<Long><Long> {
	//
	//     private LongAdder acceptTime = new LongAdder();
	//     private LongAdder offlineTimes = new LongAdder();
	//     private LongAdder closeTimes = new LongAdder();
	//
	//     public CommonSession<Long>(MockEndpointEventHandler<? extends NetEndpoint<Long>> eventHandler, int cacheSentMessageSize) {
	//         super(eventHandler, eventHandler, cacheSentMessageSize);
	//     }
	//
	//
	//     @Override
	//     protected boolean acceptTunnel(NetTunnel<Long> newTunnel) throws ValidatorFailException {
	//         acceptTime.increment();
	//         return super.acceptTunnel(newTunnel);
	//     }
	//
	//
	//     @Override
	//     protected void setOffline() {
	//         offlineTimes.increment();
	//         super.setOffline();
	//     }
	//
	//     @Override
	//     protected void setClose() {
	//         closeTimes.increment();
	//         super.setClose();
	//     }
	//
	//     public int getAcceptTime() {
	//         return acceptTime.intValue();
	//     }
	//
	//     public int getOfflineTimes() {
	//         return offlineTimes.intValue();
	//     }
	//
	//     public int getCloseTimes() {
	//         return closeTimes.intValue();
	//     }
	//
	//     public void resetAcceptTime() {
	//         this.acceptTime.reset();
	//     }
	// }

}