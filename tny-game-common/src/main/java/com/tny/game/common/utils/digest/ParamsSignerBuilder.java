// package com.tny.game.common.utils.digest;
//
// import org.slf4j.Logger;
// import org.tny.film.core.sdk.SDKParam;
//
// import java.util.Arrays;
// import java.util.Collection;
// import java.util.HashSet;
// import java.util.Map;
// import java.util.Set;
// import java.util.function.BiFunction;
// import java.util.function.BiPredicate;
// import java.util.function.Function;
//
// import static org.tny.film.access.domain.ParamsUtils.*;
// import static org.tny.film.access.domain.ParamsUtils.EQ_ENTRY_2_WORD;
// import static org.tny.film.access.domain.ParamsUtils.SORT_KEY_ORDER;
//
// /**
//  * Created by Kun Yang on 2017/7/3.
//  */
// public class ParamsSignerBuilder<K> {
//
//     private String separator = "";
//
//     private Function<Object, K> keyKeeper;
//
//     private Logger logger;
//
//     private Set<String> ignores = new HashSet<>();
//
//     private BiPredicate<String, Object> filter = (key, value) -> true;
//
//     private BiFunction<String, Object, String> entry2Word = EQ_ENTRY_2_WORD;
//
//     private Function<Map<String, ?>, Collection<String>> keyOrder = SORT_KEY_ORDER;
//
//     private ParamsVerifiable<K> verifiable;
//
//     private ParamsSignable<K> signable;
//
//     private ParamsSignerBuilder() {
//     }
//
//     public static <K> ParamsSignerBuilder<K> create() {
//         return new ParamsSignerBuilder<>();
//     }
//
//     public ParamsSignerBuilder<K> setVerifiable(ParamsVerifiable<K> verifiable) {
//         this.verifiable = verifiable;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> setSignable(ParamsSignable<K> signable) {
//         this.signable = signable;
//         return this;
//     }
//
//
//     public ParamsSignerBuilder<K> setSeparator(String separator) {
//         this.separator = separator;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> setEntry2Word(BiFunction<String, Object, String> entry2Word) {
//         this.entry2Word = entry2Word;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> setKeyOrder(Function<Map<String, ?>, Collection<String>> keyOrder) {
//         this.keyOrder = keyOrder;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> setKeyKeeper(Function<Object, K> keyKeeper) {
//         this.keyKeeper = keyKeeper;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> setLogger(Logger logger) {
//         this.logger = logger;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> setIgnores(Set<String> ignores) {
//         this.ignores = ignores;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> setFilter(BiPredicate<String, Object> filter) {
//         this.filter = filter;
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> addIgnores(Set<String> ignores) {
//         this.ignores.addAll(ignores);
//         return this;
//     }
//
//     public ParamsSignerBuilder<K> addIgnores(String... ignores) {
//         this.ignores.addAll(Arrays.asList(ignores));
//         return this;
//     }
//
//
//     public ParamsSigner<K> build() {
//         ParamsSigner<K> signer = new ParamsSigner<>();
//         signer.setIgnores(ignores)
//                 .setKeyOrder(keyOrder)
//                 .setSeparator(separator)
//                 .setEntry2Word(entry2Word)
//                 .setFilter(filter)
//                 .setKeyKeeper(keyKeeper)
//                 .setLogger(logger)
//                 .setSignable(signable)
//                 .setVerifiable(verifiable);
//         return signer;
//     }
//
// }
