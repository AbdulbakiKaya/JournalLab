package journalLabb.search.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import journalLabb.search.dto.PatientDto;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/api/patients")
@RegisterRestClient(configKey = "patient-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PatientClient {

    @GET
    List<PatientDto> getAll(@HeaderParam("Authorization") String authorization);

    @GET
    @Path("/doctor/{doctorUserId}")
    List<PatientDto> getForDoctor(@PathParam("doctorUserId") Long doctorUserId,
                                  @HeaderParam("Authorization") String authorization);
}