package journalLabb.search.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import journalLabb.search.dto.ConditionDto;
import journalLabb.search.dto.EncounterDto;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "clinical-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ClinicalClient {

    @GET
    @Path("/api/conditions/patient/{patientId}")
    List<ConditionDto> getConditionsForPatient(@PathParam("patientId") Long patientId,
                                               @HeaderParam("Authorization") String authorization);

    @GET
    @Path("/api/encounters/doctor/{doctorUserId}/date/{date}")
    List<EncounterDto> getEncountersForDoctorOnDate(@PathParam("doctorUserId") Long doctorUserId,
                                                    @PathParam("date") String date,
                                                    @HeaderParam("Authorization") String authorization);
}