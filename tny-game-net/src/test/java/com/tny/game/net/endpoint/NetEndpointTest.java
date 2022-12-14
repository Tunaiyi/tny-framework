/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.endpoint;

import com.tny.game.net.command.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.test.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.function.Predicate;

import static com.tny.game.net.transport.TestMessages.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class NetEndpointTest<E extends NetEndpoint<Long>> extends EndpointTest<E> {

    private static final int CACHE_MESSAGE_SIZE = 10;

    private static final Predicate<Message> all = (m) -> true;

    private static final Predicate<Message> gte1 = (m) -> m.getId() >= 1L;

    private static final Predicate<Message> lte3 = (m) -> m.getId() <= 3L;

    private static final Predicate<Message> gte2lte3 = (m) -> 2 <= m.getId() && m.getId() <= 3L;

    protected NetEndpointTest() {
    }

    @Override
    protected EndpointTestInstance<E> create(Certificate<Long> certificate) {
        E endpoint = newEndpoint(new CommonSessionSetting().setSendMessageCachedSize(CACHE_MESSAGE_SIZE));
        MockNetTunnel tunnel = mockTunnel(endpoint);
        if (certificate.isAuthenticated()) {
            TestAide.assertRunComplete("acceptTunnel", () -> endpoint.online(certificate, tunnel));
        }
        return new EndpointTestInstance<>(endpoint, tunnel);
    }

    private MockNetTunnel mockTunnel(E endpoint) {
        return new MockNetTunnel(endpoint, TunnelMode.SERVER);
    }

    protected abstract E newEndpoint(CommonSessionSetting S);

    @Override
    public E createNetter(Certificate<Long> certificate) {
        return create(certificate).getEndpoint();
    }

    @Override
    protected EndpointTestInstance<E> create() {
        return create(certificateId, uid);
    }

    private EndpointTestInstance<E> create(long certificateId, Long uid) {
        return create(createLoginCert(certificateId, uid));
    }

    @Override
    protected void doOffline(E endpoint) {
        endpoint.offline();
    }

    @Test
    void offline() {
        NetEndpoint<Long> loginEndpoint = create().getEndpoint();
        assertFalse(loginEndpoint.isOffline());
        loginEndpoint.offline();
        assertTrue(loginEndpoint.isOffline());
    }

    @Test
    void onUnactivated() {
        Certificate<Long> certificate = createLoginCert();
        EndpointTestInstance<E> object = create(certificate);
        E loginEndpoint = object.getEndpoint();
        MockNetTunnel loginTunnel = object.getTunnel();

        assertFalse(loginEndpoint.isOffline());
        loginEndpoint.onUnactivated(loginTunnel);
        assertEquals(EndpointStatus.ONLINE, loginEndpoint.getStatus());

        // ?????? tunnel ??????
        loginTunnel.close();
        assertEquals(EndpointStatus.OFFLINE, loginEndpoint.getStatus());

        // ?????? tunnel ??????
        loginEndpoint.onUnactivated(loginTunnel);
        assertEquals(EndpointStatus.OFFLINE, loginEndpoint.getStatus());

        // ???????????????
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        loginEndpoint.close();
        // loginEndpoint.onUnactivated(loginTunnel);
        assertEquals(EndpointStatus.CLOSE, loginEndpoint.getStatus());
    }

    @Test
    void getSendMessages() {
        EndpointTestInstance<E> object;
        List<Message> sentMessages;
        TestMessages messages;

        object = create();
        NetEndpoint<Long> e0 = object.getEndpoint();
        NetTunnel<Long> t0 = object.getTunnel();
        messages = createMessages(e0);
        messages.messagesForEach(m -> assertEquals(0, e0.getSentMessages(sm -> sm.getId() == m.getId()).size()));
        messages.contextsForEach(c -> e0.createMessage(t0.getMessageFactory(), c));
        messages.messagesForEach(m -> assertEquals(1, e0.getSentMessages(sm -> sm.getId() == m.getId()).size()));

        object = create();
        NetEndpoint<Long> e1 = object.getEndpoint();
        NetTunnel<Long> t1 = object.getTunnel();
        messages = createMessages(e1);
        // List<>messages.getMessages();

        sentMessages = e1.getSentMessages(all);
        assertTrue(sentMessages.isEmpty());
        messages.contextsForEach(c -> e1.createMessage(t1.getMessageFactory(), c));
        sentMessages = e1.getSentMessages(gte1);
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = e1.getSentMessages(all);
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = e1.getSentMessages(lte3);
        assertEquals(3, sentMessages.size());
        sentMessages = e1.getSentMessages(gte2lte3);
        assertEquals(2, sentMessages.size());

        object = create();
        NetEndpoint<Long> e2 = object.getEndpoint();
        NetTunnel<Long> t2 = object.getTunnel();
        int messageSize = 13;
        messages = new TestMessages(e2);
        for (int index = 1; index <= messageSize; index++) {
            messages.addPush("push " + index);
        }
        sentMessages = e2.getSentMessages(all);
        assertTrue(sentMessages.isEmpty());

        messages.contextsForEach(c -> e2.createMessage(t2.getMessageFactory(), c));

        sentMessages = e2.getSentMessages(lte3);
        assertTrue(sentMessages.isEmpty());

        sentMessages = e2.getSentMessages(all);
        assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());

        sentMessages = e2.getSentMessages(m -> 4 <= m.getId() && m.getId() <= 13);
        assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
    }

    //    @Override
    //    @Test
    //    public void receive() {
    //        TestMessages messages;
    //        NetEndpoint<Long> endpoint;
    //        EndpointTestInstance<E> object;
    //
    //        // ?????? receive message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        messages = createMessages(endpoint);
    //        messages.receive(endpoint);
    //        assertEquals(handler.getInputTimes(), messages.getMessageSize());
    //
    //        // ?????? send message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.offline();
    //        messages = createMessages(endpoint);
    //        messages.receive(endpoint);
    //        assertEquals(handler.getInputTimes(), messages.getMessageSize());
    //
    //        // ???????????? send message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.close();
    //        messages = createMessages(endpoint);
    //        messages.receive(endpoint);
    //        assertEquals(handler.getInputTimes(), 0);
    //
    //        // filter ?????? MessageHandleStrategy.IGNORE
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.setReceiveFilter((e, m) -> MessageHandleStrategy.IGNORE);
    //        messages = createMessages(endpoint);
    //        messages.receive(endpoint);
    //        assertEquals(handler.getInputTimes(), 0);
    //
    //        // filter ?????? MessageHandleStrategy.THROW
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.setReceiveFilter((e, m) -> MessageHandleStrategy.THROW);
    //        messages = createMessages(endpoint);
    //        NetEndpoint<Long> e1 = endpoint;
    //        TestMessages ms1 = messages;
    //        TestAide.assertRunWithException(() -> ms1.receive(e1));
    //        assertEquals(handler.getInputTimes(), 0);
    //    }
    //
    //    @Override
    //    @Test
    //    public void send() {
    //        TestMessages messages;
    //        NetEndpoint<Long> endpoint;
    //        EndpointTestInstance<E> object;
    //        MockEndpointEventsBoxHandler<E> handler;
    //
    //        // ?????? send message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        messages = createMessages(endpoint);
    //        messages.send(endpoint);
    //        assertEquals(handler.getOutputTimes(), messages.getMessageSize());
    //
    //        // ?????? send message willResponseFuture
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        messages = createMessages(endpoint);
    //        messages.forEach(p -> p.getRequestContext().willResponseFuture());
    //        messages.send(endpoint);
    //        assertEquals(handler.getOutputTimes(), messages.getMessageSize());
    //
    //        // ???????????? send message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.offline();
    //        messages = createMessages(endpoint);
    //        messages.send(endpoint);
    //        assertEquals(handler.getOutputTimes(), messages.getMessageSize());
    //
    //        // ???????????? send message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.close();
    //        messages = createMessages(endpoint);
    //        messages.send(endpoint);
    //        assertEquals(handler.getOutputTimes(), 0);
    //
    //        // filter ?????? MessageHandleStrategy.IGNORE
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.setSendFilter((e, m) -> MessageHandleStrategy.IGNORE);
    //        messages = createMessages(endpoint);
    //        messages.send(endpoint);
    //        assertEquals(handler.getOutputTimes(), 0);
    //
    //        // filter ?????? MessageHandleStrategy.THROW
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.setSendFilter((e, m) -> MessageHandleStrategy.THROW);
    //        messages = createMessages(endpoint);
    //        NetEndpoint<Long> e1 = endpoint;
    //        TestMessages ms1 = messages;
    //        TestAide.assertRunWithException(() -> ms1.send(e1));
    //        assertEquals(handler.getOutputTimes(), 0);
    //    }
    //
    //    @Override
    //    @Test
    //    public void resend() {
    //        NetEndpoint<Long> endpoint;
    //        EndpointTestInstance<E> object;
    //        MockEndpointEventsBoxHandler<E> handler;
    //
    //        // ?????? resend message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.resend(null, all);
    //        endpoint.resend(null, all);
    //        endpoint.resend(null, all);
    //        assertEquals(3, handler.getOutputTimes());
    //
    //        // close ?????? resend message
    //        object = create();
    //        endpoint = object.getEndpoint();
    //        handler = object.getHandler();
    //        endpoint.close();
    //        endpoint.resend(null, all);
    //        endpoint.resend(null, all);
    //        endpoint.resend(null, all);
    //        assertEquals(0, handler.getOutputTimes());
    //    }

    @Test
    void online() {
        MockNetTunnel loginTunnel;
        MockNetTunnel otherTunnel;
        Certificate<Long> newCertificate;
        Certificate<Long> certificate = createLoginCert();
        EndpointTestInstance<E> object;
        E loginEndpoint;

        // ????????????????????????
        object = create(createUnLoginCert());
        loginEndpoint = object.getEndpoint();
        loginTunnel = object.getTunnel();
        newCertificate = createUnLoginCert();
        assertAcceptTunnelException(loginEndpoint, newCertificate, loginTunnel);

        // ????????????
        object = create(createUnLoginCert());
        loginEndpoint = object.getEndpoint();
        loginTunnel = object.getTunnel();
        assertAcceptTunnelOk(loginEndpoint, certificate, loginTunnel);

        // ???????????? ???????????? ??????tunnel
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        loginTunnel = object.getTunnel();
        assertAcceptTunnelOk(loginEndpoint, certificate, loginTunnel);

        // ???????????? ???????????? ??????tunnel
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        newCertificate = createLoginCert();
        assertAcceptTunnelOk(loginEndpoint, newCertificate, loginTunnel);

        // ???????????? ???????????? ??????tunnel
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        newCertificate = createLoginCert();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        assertAcceptTunnelOk(loginEndpoint, newCertificate, otherTunnel);

        // ???????????? ???????????? ??????tunnel
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        loginTunnel = object.getTunnel();
        newCertificate = createLoginCert(certificateId + 1, uid);
        assertAcceptTunnelException(loginEndpoint, newCertificate, loginTunnel);

        // ???????????? ???????????? ??????tunnel
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        newCertificate = createLoginCert(certificateId + 1, uid);
        assertAcceptTunnelException(loginEndpoint, newCertificate, otherTunnel);

        // session ?????? ?????? session
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        loginEndpoint.offline();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        newCertificate = createLoginCert();
        assertAcceptTunnelOk(loginEndpoint, newCertificate, otherTunnel);

        // session ????????????
        object = create(certificate);
        loginEndpoint = object.getEndpoint();
        loginEndpoint.close();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginEndpoint, newCertificate, otherTunnel);

        // session tunnel bind fail
        object = create(createUnLoginCert());
        loginEndpoint = object.getEndpoint();
        loginTunnel = object.getTunnel();
        loginTunnel.setBindSuccess(false);
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginEndpoint, newCertificate, loginTunnel);

        // session tunnel bind fail
        object = create();
        loginEndpoint = object.getEndpoint();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        otherTunnel.setBindSuccess(false);
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginEndpoint, newCertificate, otherTunnel);

        // session tunnel bind fail
        object = create();
        loginEndpoint = object.getEndpoint();
        loginTunnel = object.getTunnel();
        loginTunnel.setBindSuccess(false);
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginEndpoint, newCertificate, loginTunnel);
    }

    private void assertAcceptTunnelOk(NetEndpoint<Long> endpoint, Certificate<Long> certificate, NetTunnel<Long> tunnel) {
        TestAide.assertRunComplete("assertLoginOk", () -> {
            endpoint.online(certificate, tunnel);
            assertTrue(endpoint.isAuthenticated());
        });
    }

    private void assertAcceptTunnelException(NetEndpoint<Long> endpoint, Certificate<Long> certificate, NetTunnel<Long> tunnel) {
        TestAide.assertRunWithException("assertLoginException", () -> endpoint.online(certificate, tunnel), ValidatorFailException.class);
    }

}