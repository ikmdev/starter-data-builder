package dev.ikm.tinkar.sandbox;

import dev.ikm.tinkar.common.util.uuid.UuidUtil;
import dev.ikm.tinkar.entity.export.ExportEntitiesController;
import dev.ikm.tinkar.starterdata.StarterData;
import dev.ikm.tinkar.starterdata.UUIDUtility;
import dev.ikm.tinkar.terms.EntityProxy;
import dev.ikm.tinkar.terms.TinkarTerm;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class SnomedLoincStarterData {

    private static final Logger LOG = Logger.getLogger(SnomedLoincStarterData.class.getSimpleName());

    private static File exportDataStore;
    private static File exportFile;
    public static void main(String args[]){

        exportDataStore = new File(args[0]);
        exportFile = new File(args[1]);

        UUIDUtility uuidUtility = new UUIDUtility();

        StarterData starterData = new StarterData(exportDataStore, uuidUtility)
                .init()
                .authoringSTAMP(
                        TinkarTerm.ACTIVE_STATE,
                        System.currentTimeMillis(),
                        TinkarTerm.USER,
                        TinkarTerm.PRIMORDIAL_MODULE,
                        TinkarTerm.PRIMORDIAL_PATH);

        configureConceptsAndPatterns(starterData, uuidUtility);
        starterData.build(); //Natively writing data to spined array
        exportStarterData(); //exports starter data to pb.zip
        starterData.shutdown();
    }

    private static void configureConceptsAndPatterns(StarterData starterData, UUIDUtility uuidUtility){


        EntityProxy.Concept snomedIdentifier = EntityProxy.Concept.make("SNOMED CT Identifier", UUID.nameUUIDFromBytes( ("org.snomed. 900000000000294009").getBytes()));
        starterData.concept(snomedIdentifier)
                .fullyQualifiedName("SNOMED CT integer identifier", TinkarTerm.PREFERRED)
                .synonym("SNOMED CT ID", TinkarTerm.PREFERRED)
                .synonym("SCTID", TinkarTerm.PREFERRED)
                .definition("Unique point of origin for identifier", TinkarTerm.PREFERRED)
                .identifier(TinkarTerm.UNIVERSALLY_UNIQUE_IDENTIFIER, snomedIdentifier.asUuidArray()[0].toString())
                .statedDefinition(List.of(TinkarTerm.IDENTIFIER_SOURCE))
                .build();

        EntityProxy.Concept snomedGrouper = EntityProxy.Concept.make("SNOMED CT Concept", uuidUtility.createUUID("SNOMED CT Concept"));
        starterData.concept(snomedGrouper)
                .fullyQualifiedName("SNOMED CT Concept", TinkarTerm.PREFERRED)
                .synonym("Health Concept", TinkarTerm.PREFERRED)
                .definition("A grouper concept that contains the SNOMED CT hierarchy", TinkarTerm.PREFERRED)
                .identifier(TinkarTerm.UNIVERSALLY_UNIQUE_IDENTIFIER, snomedGrouper.asUuidArray()[0].toString())
                .statedDefinition(List.of(TinkarTerm.ROOT_VERTEX))
                .build();

        EntityProxy.Concept snomedAuthor = EntityProxy.Concept.make("SNOMED CT LOINC Collaboration Author", uuidUtility.createUUID("SNOMED CT LOINC Collaboration Author"));
        starterData.concept(snomedAuthor)
                .fullyQualifiedName("SNOMED CT LOINC Collaboration Author", TinkarTerm.PREFERRED)
                .synonym("SNOMED CT LOINC Collaboration Author", TinkarTerm.PREFERRED)
                .definition("International Health Terminology Standards Development Organisation (IHTSDO) SNOMED CT and LOINC Author", TinkarTerm.PREFERRED)
                .identifier(TinkarTerm.UNIVERSALLY_UNIQUE_IDENTIFIER, snomedAuthor.asUuidArray()[0].toString())
                .statedDefinition(List.of(TinkarTerm.USER))
                .build();

        /* UUID from SNOMED Browser - This is the ECL query : '705114005 |LOINC Code System (qualifier value)|'  */
        EntityProxy.Concept loincIdentifier = EntityProxy.Concept.make("LOINC Number", UUID.nameUUIDFromBytes( ("org.snomed. 705114005").getBytes()));
        starterData.concept(loincIdentifier)
                .fullyQualifiedName("LOINC Number", TinkarTerm.PREFERRED)
                .synonym("LOINC Num", TinkarTerm.PREFERRED)
                .definition("Unique point of origin for identifier", TinkarTerm.PREFERRED)
                .identifier(TinkarTerm.UNIVERSALLY_UNIQUE_IDENTIFIER, loincIdentifier.asUuidArray()[0].toString())
                .statedDefinition(List.of(TinkarTerm.IDENTIFIER_SOURCE))
                .build();
    }

    private static void exportStarterData(){
        ExportEntitiesController exportEntitiesController = new ExportEntitiesController();
        try {
            exportEntitiesController.export(exportFile).get();
        } catch (ExecutionException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
