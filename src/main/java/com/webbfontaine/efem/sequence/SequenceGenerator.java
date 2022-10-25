package com.webbfontaine.efem.sequence;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;


@Component
public class SequenceGenerator {

    private KeyRepository keyRepository;

    private final LoadingCache<String,AtomicLong> sequenceCache = CacheBuilder.newBuilder().build(new CacheLoader<String,AtomicLong>() {
        @Override
        public AtomicLong load(String key) {
            return new AtomicLong(keyRepository.findLatestTrNumber(Integer.parseInt(key.split("/")[0]),key.split("/")[1]));
        }
    });

    private final LoadingCache<String,AtomicLong> sequenceRegCache = CacheBuilder.newBuilder().build(new CacheLoader<String,AtomicLong>() {
        @Override
        public AtomicLong load(String key) {
            return new AtomicLong(keyRepository.findLatestRegNumber(Integer.parseInt(key.split("/")[0]),key.split("/")[1]));
        }
    });

    private final LoadingCache<String, AtomicLong> transferSequenceCache = CacheBuilder.newBuilder().build(new CacheLoader<String, AtomicLong>() {
        @Override
        public AtomicLong load(String key) {
            return new AtomicLong(keyRepository.findLastedOrderTransferRegNumber(Integer.parseInt(key)));
        }
    });

    private final LoadingCache<String, AtomicLong> currencySequenceCache = CacheBuilder.newBuilder().build(new CacheLoader<String, AtomicLong>() {
        @Override
        public AtomicLong load(String key) {
            return new AtomicLong(keyRepository.findLastedCurrencyTransferRegNumber(Integer.parseInt(key)));
        }
    });

    private final LoadingCache<String, AtomicLong> repatriationSequenceCache = CacheBuilder.newBuilder().build(new CacheLoader<String, AtomicLong>() {
        @Override
        public AtomicLong load(String key) {
            return new AtomicLong(keyRepository.findLatestRepatNumber(Integer.parseInt(key)));
        }
    });

    public long nextRequestNumber(String key ) {
        return sequenceCache.getUnchecked(key).incrementAndGet();
    }

    public long currencyNextRequestNumber(String key) {
        return currencySequenceCache.getUnchecked(key).incrementAndGet();
    }

    public long repatriationNextRequestNumber(String key) {
        return repatriationSequenceCache.getUnchecked(key).incrementAndGet();
    }

    public long transferNextRequestNumber(String key) {
        return transferSequenceCache.getUnchecked(key).incrementAndGet();
    }

    public long nextRegistrationNumber(String key ) {
        return sequenceRegCache.getUnchecked(key).incrementAndGet();
    }

    @Autowired
    public void setKeyRepository(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

}
