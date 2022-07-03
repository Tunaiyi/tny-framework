package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import io.etcd.jetcd.*;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.function.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 06:05
 **/
public class EtcdNamespaceExplorerFactory implements NamespaceExplorerFactory {

    private final EtcdConfig etcdConfig;

    private final ObjectCodecAdapter objectCodecAdapter;

    private final Consumer<ClientBuilder> onBuild;

    private volatile EtcdNamespaceExplorer namespaceExplorer;

    public EtcdNamespaceExplorerFactory(EtcdConfig etcdConfig, Consumer<ClientBuilder> onBuild, ObjectCodecAdapter objectCodecAdapter) {
        this.etcdConfig = etcdConfig;
        this.onBuild = onBuild;
        this.objectCodecAdapter = objectCodecAdapter;
    }

    @Override
    public NamespaceExplorer create() {
        if (namespaceExplorer != null) {
            return namespaceExplorer;
        }
        synchronized (this) {
            if (namespaceExplorer != null) {
                return namespaceExplorer;
            }
            ClientBuilder builder = Client.builder();
            Charset charset = Charset.forName(ifBlank(etcdConfig.getCharset(), "utf-8"));
            builder.endpoints(etcdConfig.getEndpoints().toArray(new String[0]));
            setIfNotBlank(etcdConfig.getUser(), builder::user, charset);
            setIfNotBlank(etcdConfig.getPassword(), builder::password, charset);
            setIfNotBlank(etcdConfig.getNamespace(), builder::namespace, charset);
            setIfNotBlank(etcdConfig.getAuthority(), builder::authority);
            setIfNotBlank(etcdConfig.getLoadBalancerPolicy(), builder::loadBalancerPolicy);
            setIfNotNull(etcdConfig.getMaxInboundMessageSize(), builder::maxInboundMessageSize);
            setLongIfNotNull(etcdConfig.getRetryDelay(), builder::retryDelay);
            setLongIfNotNull(etcdConfig.getRetryMaxDelay(), builder::retryMaxDelay);
            setDurationIfNotNull(etcdConfig.getKeepaliveTime(), builder::keepaliveTime);
            setDurationIfNotNull(etcdConfig.getKeepaliveTimeout(), builder::keepaliveTimeout);
            setIfNotNull(etcdConfig.isKeepaliveWithoutCalls(), builder::keepaliveWithoutCalls);
            setDurationIfNotNull(etcdConfig.getRetryMaxDuration(), builder::retryMaxDuration);
            setDurationIfNotNull(etcdConfig.getConnectTimeout(), builder::connectTimeout);
            setIfNotNull(etcdConfig.isWaitForReady(), builder::waitForReady);
            seHeaderIf(etcdConfig.getHeaders(), builder::header);
            seHeaderIf(etcdConfig.getAuthHeaders(), builder::authHeader);
            if (onBuild != null) {
                onBuild.accept(builder);
            }
            Client client = builder.build();
            namespaceExplorer = new EtcdNamespaceExplorer(client, objectCodecAdapter, charset);
        }
        return namespaceExplorer;
    }

    private void shutdown() {
        synchronized (this) {
            namespaceExplorer.shutdown();
        }
    }

    private void seHeaderIf(Map<String, String> header, BiConsumer<String, String> setter) {
        if (header != null) {
            header.forEach(setter);
        }
    }

    private void setIfNotBlank(String value, Consumer<ByteSequence> setter, Charset charset) {
        if (StringUtils.isNotBlank(value)) {
            setter.accept(ByteSequence.from(value, charset));
        }
    }

    private void setIfNotBlank(String value, Consumer<String> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private <T> void setLongIfNotNull(Long value, Consumer<Long> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private <T> void setDurationIfNotNull(Long value, Consumer<Duration> setter) {
        if (value != null) {
            setter.accept(Duration.ofMillis(value));
        }
    }

    private <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private String ifBlank(String value, String defaultValue) {
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

}
