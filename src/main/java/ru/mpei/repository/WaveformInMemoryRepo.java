package ru.mpei.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mpei.model.WaveformModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class WaveformInMemoryRepo implements CrudRepository<WaveformModel, String> {

    Map<String, WaveformModel> waveforms = new HashMap<>();

    @Override
    public <S extends WaveformModel> S save(S entity) {
        waveforms.put(entity.getCaseName(), entity);
        return entity;
    }

    @Override
    public <S extends WaveformModel> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            waveforms.put(entity.getCaseName(), entity);
        }
        return entities;
    }

    @Override
    public Optional<WaveformModel> findById(String s) {
        return Optional.ofNullable(waveforms.get(s));
    }

    @Override
    public boolean existsById(String s) {
        return waveforms.containsKey(s);
    }

    @Override
    public Iterable<WaveformModel> findAll() {
        return waveforms.values();
    }

    @Override
    public Iterable<WaveformModel> findAllById(Iterable<String> strings) {
        Map<String, WaveformModel> found = new HashMap<>();
        for (String s : strings) {
            found.put(s, waveforms.get(s));
        }
        return found.values();
    }

    @Override
    public long count() {
        return waveforms.size();
    }

    @Override
    public void deleteById(String s) {
        waveforms.remove(s);
    }

    @Override
    public void delete(WaveformModel entity) {
        waveforms.values().remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        waveforms.keySet().removeAll(Collections.singleton(strings));
    }

    @Override
    public void deleteAll(Iterable<? extends WaveformModel> entities) {
        waveforms.values().removeAll(Collections.singleton(entities));
    }

    @Override
    public void deleteAll() {
        waveforms.clear();
    }
}
