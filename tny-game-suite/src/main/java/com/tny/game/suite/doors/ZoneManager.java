package com.tny.game.suite.doors;


import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.suite.cluster.ServerNode;
import com.tny.game.suite.doors.dao.EntryDAO;
import com.tny.game.suite.doors.dao.EntryVO;
import com.tny.game.suite.doors.dao.PlatformDAO;
import com.tny.game.suite.doors.dao.PlatformVO;
import com.tny.game.suite.doors.dao.ZoneDAO;
import com.tny.game.suite.doors.dao.ZoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Profile({"god_admin", "god_access"})
public class ZoneManager {

    protected volatile Map<Integer, Zone> zoneMap = new CopyOnWriteMap<>();

    protected volatile Map<Integer, Platform> platforMap = new CopyOnWriteMap<>();
    protected volatile Map<String, Platform> markPlatforMap = new CopyOnWriteMap<>();

    @Autowired
    protected ZoneDAO zoneDAO;

    @Autowired
    protected PlatformDAO platformDAO;

    @Autowired
    protected EntryDAO entryDAO;

    @Autowired
    protected DoorsCluster doorCluster;

    @PostConstruct
    private void init() {
        this.reload();
    }

    public Map<Integer, Zone> getAllZoneMap() {
        return Collections.unmodifiableMap(this.zoneMap);
    }

    public List<Zone> getAllZone() {
        return new ArrayList<>(this.zoneMap.values());
    }

    public Map<Integer, Platform> getAllPlatformMap() {
        return Collections.unmodifiableMap(this.platforMap);
    }

    public List<Platform> getAllPlatform() {
        return new ArrayList<>(this.platforMap.values());
    }

    public Zone getZone(int zoneID) {
        return this.zoneMap.get(zoneID);
    }

    protected List<Entry> getEntriesByDB(int zoneID) {
        List<Entry> entries = new ArrayList<>();
        List<EntryVO> voList = this.entryDAO.getByZoneID(zoneID);
        for (EntryVO vo : voList) {
            Entry entry = this.vo2Entry(vo);
            if (entry != null)
                entries.add(entry);
        }
        return entries;
    }

    protected ZoneVO zone2VO(Zone zone) {
        ZoneVO vo = new ZoneVO();
        vo.setZoneID(zone.getZoneID());
        vo.setName(zone.getName());
        vo.setZoneID(zone.getZoneID());
        vo.setVersion(zone.getVersion());
        return vo;
    }

    protected Zone vo2Zone(ZoneVO vo) {
        Zone zone = new Zone(vo.getZoneID(), vo.getName(), vo.getVersion());
        List<Entry> entrys = this.getEntriesByDB(vo.getZoneID());
        zone.setEntries(new HashSet<>(entrys));
        return zone;
    }

    protected EntryVO entry2VO(Entry entry) {
        EntryVO vo = new EntryVO();
        vo.setName(entry.getName());
        vo.setNumber(entry.getNumber());
        vo.setZoneID(entry.getZoneID());
        vo.setServerID(entry.getServerID());
        vo.setOnlineType(entry.getOnlineType().getID());
        vo.setEntryState(entry.getEntryState().getID());
        return vo;
    }

    protected Entry vo2Entry(EntryVO vo) {
        ServerNode node = this.doorCluster.getServerNode(vo.getServerID());
        if (node == null)
            return null;
        Entry entry = new Entry();
        entry.setName(vo.getName());
        entry.setNumber(vo.getNumber());
        entry.setZoneID(vo.getZoneID());
        entry.setServer(node);
        entry.setEntryState(EntryState.get(vo.getEntryState()));
        entry.setOnlineType(OnlineType.get(vo.getOnlineType()));
        return entry;
    }

    public boolean saveZone(Zone zone) {
        ZoneVO vo = this.zone2VO(zone);
        if (this.zoneDAO.save(vo) > 0) {
            this.zoneMap.put(zone.getZoneID(), zone);
            return true;
        }
        return false;
    }

    public boolean deleteZone(int zoneID) {
        Zone zone = this.zoneMap.remove(zoneID);
        if (zone != null)
            return this.zoneDAO.delect(zoneID) > 0;
        return false;
    }

    public boolean savePlatform(Platform platform) {
        PlatformVO vo = this.platform2VO(platform);
        if (this.platformDAO.save(vo) > 0) {
            this.platforMap.put(platform.getID(), platform);
            return true;
        }
        return false;
    }

    public void savePlatform(Collection<Platform> platforms) {
        List<PlatformVO> vos = new ArrayList<>();
        for (Platform platform : platforms) {
            PlatformVO vo = this.platform2VO(platform);
            this.platforMap.put(platform.getID(), platform);
            vos.add(vo);
        }
        this.platformDAO.save(vos);
    }

    public void deletePlatform(Integer id) {
        Platform platform = this.platforMap.remove(id);
        if (platform != null)
            this.platformDAO.delect(platform.getID());
    }

    public int saveEntries(int zoneID, Collection<Entry> entries) {
        Zone zone = this.getZone(zoneID);
        if (zone == null)
            return 0;
        for (Entry entry : entries) {
            zone.updateEntry(entry);
        }
        List<EntryVO> voList = new ArrayList<>();
        for (Entry entry : entries)
            voList.add(this.entry2VO(entry));
        return this.entryDAO.save(voList);
    }

    public boolean deleteEntries(int zoneID, Collection<Integer> numbers) {
        Zone zone = this.getZone(zoneID);
        if (zone == null)
            return false;
        for (Integer num : numbers)
            zone.removeEntry(num);
        this.entryDAO.delect(zone.getZoneID(), numbers);
        return true;
    }

    protected PlatformVO platform2VO(Platform platform) {
        PlatformVO vo = new PlatformVO();
        vo.setId(platform.getID());
        vo.setMark(platform.getMark());
        vo.setName(platform.getName());
        vo.setZoneID(platform.getZoneID());
        return vo;
    }

    protected Platform vo2Platform(PlatformVO vo) {
        Platform platform = new Platform();
        platform.setID(vo.getId());
        platform.setMark(vo.getMark());
        platform.setName(vo.getName());
        Zone zone = this.getZone(vo.getZoneID());
        platform.setZone(zone);
        return platform;
    }

    public void reloadZone() {
        Map<Integer, Zone> zoneMap = new CopyOnWriteMap<>();
        List<ZoneVO> zoneVOs = this.zoneDAO.getAll();
        for (ZoneVO vo : zoneVOs) {
            Zone zone = this.vo2Zone(vo);
            zoneMap.put(zone.getZoneID(), zone);
        }
        this.zoneMap = zoneMap;
    }

    public void reloadPlatform() {
        Map<Integer, Platform> platforMap = new CopyOnWriteMap<Integer, Platform>();
        Map<String, Platform> markPlatforMap = new CopyOnWriteMap<String, Platform>();
        List<PlatformVO> platformVOs = this.platformDAO.getAll();
        for (PlatformVO vo : platformVOs) {
            Platform platform = this.vo2Platform(vo);
            platforMap.put(platform.getID(), platform);
            markPlatforMap.put(platform.getMark(), platform);
        }
        this.platforMap = platforMap;
        this.markPlatforMap = markPlatforMap;
    }

    public void reload() {
        this.reloadZone();
        this.reloadPlatform();
    }

    public Platform getPlatform(String mark) {
        return this.markPlatforMap.get(mark);
    }

    public Platform getPlatform(int id) {
        return this.platforMap.get(id);
    }

}
