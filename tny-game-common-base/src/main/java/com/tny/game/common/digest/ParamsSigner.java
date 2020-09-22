package com.tny.game.common.digest;// package com.tny.game.common.utils.digest;
//
// import org.apache.commons.lang3.StringUtils;
// import org.slf4j.Logger;
// import org.tny.film.core.sdk.SDKParam;
//
// import java.util.Collection;
// import java.util.HashSet;
// import java.util.Map;
// import java.util.Set;
// import java.util.function.BiFunction;
// import java.util.function.BiPredicate;
// import java.util.function.Function;
// import java.util.function.Supplier;
//
// /**
//  * Created by Kun Yang on 2017/7/3.
//  */
// public class ParamsSigner<K> {
//
//     private Logger logger;
//
//     private String separator = "";
//
//     private Function<Object, K> keyCreator;
//
//     private Set<String> ignores = new HashSet<>();
//
//     private BiPredicate<String, Object> filter;
//
//     private Function<Map<String, ?>, Collection<String>> keyOrder;
//
//     private BiFunction<String, Object, String> entry2Word;
//
//     private ParamsVerifiable<K> verifiable;
//
//     private ParamsSignable<K> signable;
//
//     ParamsSigner() {
//     }
//
//     public boolean verify(String sign, Map<String, ?> data, Supplier<K> keySupplier) throws Throwable {
//         String paramWords = link(data, sdkParam);
//         this.logSignParam(paramWords);
//         K key = this.keyCreator.apply(sdkParam);
//         String localSign = signable.sign(data, paramWords, key);
//         if (verifiable.verify(sign, localSign, key)) {
//             logSignSucc(sign);
//             return true;
//         }
//         this.logSignFail(localSign, sign, paramWords);
//         return false;
//     }
//
//     public String sign(Map<String, ?> data, SDKParam sdkParam) throws Throwable {
//         String paramWords = link(data, sdkParam);
//         this.logSignParam(paramWords);
//         K key = this.keyCreator.apply(sdkParam);
//         return signable.sign(data, paramWords, key);
//     }
//
//     private String link(Map<String, ?> data, SDKParam sdkParam) {
//         Collection<String> paramKeys = keyOrder.apply(data);
//         StringBuilder builder = new StringBuilder(256);
//         boolean hasSeparator = StringUtils.isNoneBlank(separator);
//         for (String key : paramKeys) {
//             if (this.ignores.contains(key))
//                 continue;
//             Object value = data.get(key);
//             if (!filter.test(key, value.toString()))
//                 continue;
//             String entryWord = entry2Word.apply(key, value.toString());
//             builder.append(entryWord).append(separator);
//         }
//         if (hasSeparator)
//             builder.deleteCharAt(builder.length() - separator.length());
//         return builder.toString();
//     }
//
//
//     ParamsSigner<K> setLogger(Logger logger) {
//         this.logger = logger;
//         return this;
//     }
//
//     ParamsSigner<K> setSeparator(String separator) {
//         this.separator = separator;
//         return this;
//     }
//
//     ParamsSigner<K> setKeyKeeper(Function<SDKParam, K> keyCreator) {
//         this.keyCreator = keyCreator;
//         return this;
//     }
//
//     ParamsSigner<K> setIgnores(Set<String> ignores) {
//         this.ignores = ignores;
//         return this;
//     }
//
//     ParamsSigner<K> setFilter(BiPredicate<String, Object> filter) {
//         this.filter = filter;
//         return this;
//     }
//
//     ParamsSigner<K> setKeyOrder(Function<Map<String, ?>, Collection<String>> keyOrder) {
//         this.keyOrder = keyOrder;
//         return this;
//     }
//
//     ParamsSigner<K> setEntry2Word(BiFunction<String, Object, String> entry2Word) {
//         this.entry2Word = entry2Word;
//         return this;
//     }
//
//     ParamsSigner<K> setVerifiable(ParamsVerifiable<K> verifiable) {
//         this.verifiable = verifiable;
//         return this;
//     }
//
//     ParamsSigner<K> setSignable(ParamsSignable<K> signable) {
//         this.signable = signable;
//         return this;
//     }
//
//     private void logSignSucc(String sign) {
//         if (this.logger.isDebugEnabled())
//             this.logger.debug("验签 | 验签成功 {}", sign);
//     }
//
//     private void logSignFail(String local, String remote, Object param) {
//         this.logger.warn("验签 | 验签失败 | [本地 : {}] [远程 : {}] \n参数:{} ", local, remote, param);
//     }
//
//     private void logSignParam(Object params) {
//         if (this.logger.isDebugEnabled())
//             this.logger.debug("验签 | params : {} ", params);
//     }
//
// }
