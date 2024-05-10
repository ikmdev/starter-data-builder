package dev.ikm.tinkar.test;

import dev.ikm.tinkar.common.service.CachingService;
import dev.ikm.tinkar.common.service.PrimitiveData;
import dev.ikm.tinkar.common.service.ServiceKeys;
import dev.ikm.tinkar.common.service.ServiceProperties;
import dev.ikm.tinkar.common.util.functional.TriConsumer;
import dev.ikm.tinkar.coordinate.stamp.StateSet;
import dev.ikm.tinkar.coordinate.stamp.calculator.Latest;
import dev.ikm.tinkar.coordinate.stamp.calculator.RelativePosition;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculator;
import dev.ikm.tinkar.entity.*;
import dev.ikm.tinkar.entity.graph.DiTreeVersion;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class QueryByTimeTest implements StampCalculator{

    @BeforeEach
    public void init() {
        File dataStore = new File(System.getProperty("user.home") + "/Solor/starter-data-export");
        String controllerName = "Open SpinedArrayStore";
        CachingService.clearAll();
        ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, dataStore);
        PrimitiveData.selectControllerByName(controllerName);
        PrimitiveData.start();
    }
    @Test
    public void RetrieveByTime() {
        int patternNid = PrimitiveData.get().nidForUuids(Lists.immutable.of(UUID.fromString("a4de0039-2625-5842-8a4c-d1ce6aebf021")));

        String time = "2020-10-22T12:31:04";


        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Stream<Latest<SemanticEntityVersion>> filteredVersionsStream =
                findAllMembershipPatternsByTime(patternNid, String.valueOf(timestamp));

        filteredVersionsStream.forEach(latestVersion -> {
            if (latestVersion.isPresent()) {
                SemanticEntityVersion semanticVersion = latestVersion.get();
                System.out.println("Latest version for SemanticEntity with NID " +
                        semanticVersion.nid() + " at time " + time + ": " + semanticVersion);
            }
        });
    }

    Stream<Latest<SemanticEntityVersion>> findAllMembershipPatternsByTime(int patternNid, String time) {
        long targetTimestamp = Long.parseLong(time);

        return streamLatestVersionForPattern(patternNid)
                .filter(latestVersion -> {
                    if (latestVersion.isPresent()) {
                        SemanticEntityVersion semanticVersion = latestVersion.get();
                        return semanticVersion.stamp().time() > targetTimestamp;
                    }
                    return false;
                });
    }


    @Override
    public Stream<Latest<SemanticEntityVersion>> streamLatestVersionForPattern(int patternNid) {
        int[] semanticNids = EntityService.get().semanticNidsOfPattern(patternNid);
        System.out.println(Arrays.toString(semanticNids));
        return Arrays.stream(semanticNids)
                .mapToObj(semanticNid -> latest(semanticNid));
    }

    @Override
    public <V extends EntityVersion> Latest<V> latest(int nid) {
        Entity<V> entity = EntityService.get().getEntityFast(nid);

        if (entity != null && !entity.versions().isEmpty()) {
            V latestVersion = entity.versions().get(0);

            for (V version : entity.versions()) {
                if (version.time() > latestVersion.time()) {
                    latestVersion = version;
                }
            }

            return Latest.of(latestVersion);
        }
        return null;
    }

    @Override
    public <V extends EntityVersion> List<DiTreeVersion<V>> getVersionGraphList(Entity<V> entity) {
        return null;
    }

    @Override
    public <V extends EntityVersion> Latest<V> latest(Entity<V> entity) {
        return null;
    }

    @Override
    public StateSet allowedStates() {
        return null;
    }

    @Override
    public RelativePosition relativePosition(int i, int i1) {
        return null;
    }

    @Override
    public void forEachSemanticVersionOfPattern(int i, BiConsumer<SemanticEntityVersion, PatternEntityVersion> biConsumer) {

    }

    @Override
    public void forEachSemanticVersionOfPatternParallel(int i, BiConsumer<SemanticEntityVersion, PatternEntityVersion> biConsumer) {

    }

    @Override
    public void forEachSemanticVersionForComponent(int i, BiConsumer<SemanticEntityVersion, EntityVersion> biConsumer) {

    }

    @Override
    public void forEachSemanticVersionForComponentOfPattern(int i, int i1, TriConsumer<SemanticEntityVersion, EntityVersion, PatternEntityVersion> triConsumer) {

    }

    @Override
    public void forEachSemanticVersionWithFieldsForComponent(int i, TriConsumer<SemanticEntityVersion, ImmutableList<? extends Field>, EntityVersion> triConsumer) {

    }

    @Override
    public Latest<PatternEntityVersion> latestPatternEntityVersion(int i) {
        return null;
    }

    @Override
    public OptionalInt getIndexForMeaning(int i, int i1) {
        return null;
    }

    @Override
    public OptionalInt getIndexForPurpose(int i, int i1) {
        return null;
    }

    @Override
    public <T> Latest<Field<T>> getFieldForSemantic(Latest<SemanticEntityVersion> latest, int i, FieldCriterion fieldCriterion) {
        return null;
    }

    @Override
    public <T> Latest<Field<T>> getFieldForSemantic(int i, int i1, FieldCriterion fieldCriterion) {
        return null;
    }
}
