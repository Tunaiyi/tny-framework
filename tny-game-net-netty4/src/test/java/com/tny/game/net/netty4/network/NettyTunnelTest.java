package com.tny.game.net.netty4.network;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.*;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NettyTunnelTest<E extends NetEndpoint<Long>, T extends BaseNetTunnel<Long, E, ?>, ME extends MockNetEndpoint>
		extends NetTunnelTest<T, ME> {

	protected EmbeddedChannel mockChannel() {
		return new MockChannel(new InetSocketAddress(8080), new InetSocketAddress(8081));
	}

	@Test
	public void remoteAddress() {
		T tunnel = createBindTunnel();
		assertNotNull(tunnel.getRemoteAddress());
	}

	@Test
	public void localAddress() {
		T tunnel = createBindTunnel();
		assertNotNull(tunnel.getLocalAddress());
	}

	@Test
	public void isClosed() {
		T tunnel = createBindTunnel();
		assertFalse(tunnel.isClosed());
		assertTrue(tunnel.isActive());
		assertFalse(tunnel.isClosed());
		tunnel.close();
		assertTrue(tunnel.isClosed());
		assertFalse(tunnel.isActive());
	}

	@Test
	public void bind() {
		NetTunnel<Long> tunnel;
		MockNetEndpoint endpoint;
		TunnelTestInstance<T, ME> object;
		// 正常绑定
		object = create(createUnLoginCert(), false);
		tunnel = object.getTunnel();
		if (tunnel.getMode() == TunnelMode.CLIENT) {
			endpoint = object.getEndpoint();
			endpoint.online(createLoginCert(), tunnel);
			assertTrue(tunnel.bind(endpoint));
			assertTrue(tunnel.isAuthenticated());
		} else {
			endpoint = createEndpoint();
			assertTrue(tunnel.bind(endpoint));
			assertTrue(tunnel.isAuthenticated());
		}

		// 重复绑定 同一终端
		assertTrue(tunnel.bind(endpoint));
		assertTrue(tunnel.isAuthenticated());

		// 重复绑定 不同终端
		endpoint = createEndpoint();
		assertFalse(tunnel.bind(endpoint));

		// 接受未认证凭证
		tunnel = createUnbindTunnel();
		endpoint = createEndpoint(createUnLoginCert());
		assertFalse(tunnel.bind(endpoint));

	}

	@Test
	public void open() {
		T tunnel;

		// 正常开启
		tunnel = createTunnel(createLoginCert(), false);
		assertFalse(tunnel.isActive());
		assertTrue(tunnel.open());
		assertTrue(tunnel.isActive());
		assertTrue(tunnel.isOpen());

		// 重复开启
		assertTrue(tunnel.open());
		assertTrue(tunnel.isActive());
		assertTrue(tunnel.isOpen());

		// 关闭再打开
		tunnel = createBindTunnel();
		tunnel.close();
		assertFalse(tunnel.open());
		assertTrue(tunnel.isClosed());
		assertFalse(tunnel.isActive());
		assertFalse(tunnel.isOpen());

		// 断开再打开
		tunnel = createTunnel(createLoginCert(), true);
		tunnel.disconnect();
		if (tunnel.getMode() == TunnelMode.SERVER) {
			assertFalse(tunnel.open());
			assertFalse(tunnel.isClosed());
			assertFalse(tunnel.isActive());
			assertFalse(tunnel.isOpen());
		} else {
			assertTrue(tunnel.open());
			assertFalse(tunnel.isClosed());
			assertTrue(tunnel.isActive());
			assertTrue(tunnel.isOpen());
		}
	}

	@Test
	public void disconnect() {
		T tunnel;

		// 正常开启
		tunnel = createBindTunnel();
		assertTrue(tunnel.isActive());
		assertTrue(tunnel.isOpen());
		tunnel.disconnect();
		assertFalse(tunnel.isActive());
		assertFalse(tunnel.isOpen());

		// 重复开启
		tunnel.disconnect();
		assertFalse(tunnel.isActive());
		assertFalse(tunnel.isOpen());

	}

	@Test
	public void close() {
		T tunnel;
		tunnel = createBindTunnel();

		// 正常关闭
		assertFalse(tunnel.isClosed());
		assertTrue(tunnel.isActive());
		tunnel.close();
		assertTrue(tunnel.isClosed());
		assertFalse(tunnel.isActive());

		tunnel.close();
		assertTrue(tunnel.isClosed());
		assertFalse(tunnel.isActive());
	}

	@Override
	@Test
	public void ping() {
		testPingPong(1, T::ping, MessageMode.PING);
	}

	@Override
	@Test
	public void pong() {
		testPingPong(1, T::pong, MessageMode.PONG);
	}

	@Override
	@Test
	public void receive() {
		T tunnel;
		TestMessages messages;
		ME endpoint;
		TunnelTestInstance<T, ME> object;

		// 接受Message
		object = create();
		tunnel = object.getTunnel();
		endpoint = object.getEndpoint();
		messages = new TestMessages(tunnel)
				.addPush("push")
				.addRequest("request")
				.addResponse("response", 1);
		messages.receive(tunnel);
		assertTrue(CollectionUtils.containsAll(messages.getMessages(), endpoint.getReceiveQueue()));

		// 关闭 接受Message
		object = create();
		tunnel = object.getTunnel();
		endpoint = object.getEndpoint();
		messages = new TestMessages(tunnel)
				.addPush("push")
				.addRequest("request")
				.addResponse("response", 1);
		tunnel.close();
		messages.receive(tunnel);
		assertTrue(CollectionUtils.containsAll(messages.getMessages(), endpoint.getReceiveQueue()));

	}

	@Override
	@Test
	public void send() {
		T tunnel;
		TestMessages messages;
		ME endpoint;
		TunnelTestInstance<T, ME> object;

		// 接受Message
		object = create();
		tunnel = object.getTunnel();
		endpoint = object.getEndpoint();
		messages = new TestMessages(tunnel)
				.addPush("push")
				.addRequest("request")
				.addResponse("response", 1);
		messages.send(tunnel);
		assertTrue(CollectionUtils.containsAll(messages.getMessages(), endpoint.getReceiveQueue()));

		// 关闭 接受Message
		object = create();
		tunnel = object.getTunnel();
		endpoint = object.getEndpoint();
		messages = new TestMessages(tunnel)
				.addPush("push")
				.addRequest("request")
				.addResponse("response", 1);
		tunnel.close();
		messages.send(tunnel);
		assertTrue(endpoint.getReceiveQueue().isEmpty());
	}

	@Test
	public void write() {
		T tunnel;
		EmbeddedChannel channel;
		TestMessages messages;

		// 写出 message 成功
		tunnel = createBindTunnel();
		channel = embeddedChannel(tunnel);
		messages = TestMessages.createMessages(tunnel);
		messages.write(tunnel, f -> {
			assertTrue(f.isDone());
			assertFalse(f.isCompletedExceptionally());
		});
		assertTrue(CollectionUtils.containsAll(messages.getMessages(), channel.outboundMessages()));

		// 写出 message 成功
		tunnel = createBindTunnel();
		channel = embeddedChannel(tunnel);
		messages = TestMessages.createMessages(tunnel);
		channel.close();
		messages.write(tunnel, f -> {
			assertTrue(f.isDone());
			assertFalse(f.isCompletedExceptionally());
		});
		assertTrue(channel.outboundMessages().isEmpty());

		// 写出 message 非 NettyWriteMessagePromise 异常
		tunnel = createBindTunnel();
		channel = embeddedChannel(tunnel);
		messages = TestMessages.createMessages(tunnel);
		T t1 = tunnel;
		messages.messagesForEach(m -> {
			try {
				t1.write(m, new MessageWriteAwaiter());
				fail("write success");
			} catch (TunnelException e) {
				assertTrue(true);
			} catch (Throwable e) {
				fail("write success " + e);
			}
		});
		assertTrue(CollectionUtils.containsAll(messages.getMessages(), channel.outboundMessages()));

		//     // 写出 message 异常
		//     tunnel = createLoginTunnel();
		//     channel = mockTunnelChannel(tunnel);
		//     message = mockAs(Message.class);
		//     when(message.getMode()).thenReturn(MessageMode.PUSH);
		//     when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
		//     Thread.sleep(sleepTime);
		//     tunnel.write(message);
		//     verify(channel, times(1)).writeAndFlush(eq(message));
		//     verifyNoMoreInteractions(channel);
		//
		//     // 写出 message callback 成功
		//     tunnel = createLoginTunnel();
		//     channel = mockTunnelChannel(tunnel);
		//     message = mockAs(Message.class);
		//     future = mockAs(ChannelFuture.class);
		//     when(message.getMode()).thenReturn(MessageMode.PUSH);
		//     when(channel.writeAndFlush(message)).thenReturn(future);
		//     when(future.isSuccess()).thenReturn(true);
		//     Thread.sleep(sleepTime);
		//     tunnel.write(message, callback);
		//     verify(channel, times(1)).writeAndFlush(eq(message));
		//     verify(future, times(1)).addListener(listener.capture());
		//     listener.getValue().operationComplete(future);
		//     verify(future, times(1)).isSuccess();
		//     assertTrue(writeResult.get());
		//     verifyNoMoreInteractions(channel, future);
		//     writeResult.set(false);
		//
		//     // 写出 message callback 失败
		//     writeResult.set(true);
		//     tunnel = createLoginTunnel();
		//     channel = mockTunnelChannel(tunnel);
		//     message = mockAs(Message.class);
		//     future = mockAs(ChannelFuture.class);
		//     when(message.getMode()).thenReturn(MessageMode.PUSH);
		//     when(channel.writeAndFlush(message)).thenReturn(future);
		//     when(future.isSuccess()).thenReturn(false);
		//     Thread.sleep(sleepTime);
		//     tunnel.write(message, callback);
		//     verify(channel, times(1)).writeAndFlush(eq(message));
		//     verify(future, times(1)).addListener(listener.capture());
		//     listener.getValue().operationComplete(future);
		//     verify(future, times(1)).isSuccess();
		//     verify(future, times(1)).cause();
		//     assertFalse(writeResult.get());
		//     verifyNoMoreInteractions(channel, future);
		//     writeResult.set(false);
		//
		//     // 写出 message callback channel 异常
		//     writeResult.set(true);
		//     tunnel = createLoginTunnel();
		//     channel = mockTunnelChannel(tunnel);
		//     message = mockAs(Message.class);
		//     when(message.getMode()).thenReturn(MessageMode.PUSH);
		//     when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
		//     Thread.sleep(sleepTime);
		//     tunnel.write(message, callback);
		//     verify(channel, times(1)).writeAndFlush(eq(message));
		//     verifyNoMoreInteractions(channel);
		//     assertFalse(writeResult.get());
		// }
		//     channel = mockTunnelChannel(tunnel);
		//     message = mockAs(Message.class);
		//     when(message.getMode()).thenReturn(MessageMode.PUSH);
		//     when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
		//     Thread.sleep(sleepTime);
		//     tunnel.write(message, callback);
		//     verify(channel, times(1)).writeAndFlush(eq(message));
		//     verifyNoMoreInteractions(channel);
		//     assertFalse(writeResult.get());
	}

	protected abstract EmbeddedChannel embeddedChannel(T tunnel);

	private void testPingPong(int times, Consumer<T> consumer, MessageMode mode) {
		T activeTunnel = createBindTunnel();
		T closeTunnel = createBindTunnel();
		closeTunnel.close();
		for (int i = 0; i < times; i++) {
			consumer.accept(activeTunnel);
			consumer.accept(closeTunnel);
		}
		assertPingPong(times, activeTunnel, mode);
		assertPingPong(0, closeTunnel, mode);
	}

	private void assertPingPong(int times, T tunnel, MessageMode mode) {
		EmbeddedChannel channel = embeddedChannel(tunnel);
		assertEquals(times, channel.outboundMessages().size());
		for (Object value : channel.outboundMessages()) {
			assertTrue(value instanceof TickMessage);
			assertEquals(mode, Objects.requireNonNull(as(value, TickMessage.class)).getMode());
		}
	}

}