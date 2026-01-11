package journalLabb.search;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import journalLabb.search.client.ClinicalClient;
import journalLabb.search.client.PatientClient;
import journalLabb.search.dto.ConditionDto;
import journalLabb.search.dto.EncounterDto;
import journalLabb.search.dto.PatientDto;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchResource {

    @RestClient
    PatientClient patientClient;

    @RestClient
    ClinicalClient clinicalClient;

    private static boolean containsIgnoreCase(String haystack, String needle) {
        if (needle == null || needle.isBlank()) return true;
        if (haystack == null) return false;
        return haystack.toLowerCase().contains(needle.toLowerCase());
    }

    @GET
    @Path("/patients")
    public Response searchPatients(
            @HeaderParam("Authorization") String authorization,
            @QueryParam("name") String name,
            @QueryParam("condition") String condition,
            @QueryParam("personalNumber") String personalNumber,
            @QueryParam("limit") @DefaultValue("50") int limit
    ) {
        if (authorization == null || authorization.isBlank()) {
            return Response.status(401).entity(Map.of("error", "Missing Authorization header")).build();
        }

        List<PatientDto> patients = patientClient.getAll(authorization);

        List<PatientDto> filtered = patients.stream()
                .filter(p -> {
                    String fullName = (p.firstName == null ? "" : p.firstName) + " " + (p.lastName == null ? "" : p.lastName);

                    boolean okName = (name == null || name.isBlank()) || containsIgnoreCase(fullName, name);
                    boolean okPn = (personalNumber == null || personalNumber.isBlank()) || containsIgnoreCase(p.personalNumber, personalNumber);

                    return okName && okPn;
                })
                .limit(Math.max(limit, 1))
                .collect(Collectors.toList());

        if (condition == null || condition.isBlank()) {
            return Response.ok(filtered).build();
        }

        List<PatientDto> conditionMatched = new ArrayList<>();
        for (PatientDto p : filtered) {
            List<ConditionDto> conditions = clinicalClient.getConditionsForPatient(p.id, authorization);

            boolean anyMatch = conditions.stream().anyMatch(c ->
                    containsIgnoreCase(c.text, condition) || containsIgnoreCase(c.severity, condition)
            );

            if (anyMatch) conditionMatched.add(p);
        }

        return Response.ok(conditionMatched).build();
    }

    @GET
    @Path("/doctor/{doctorUserId}")
    public Response searchByDoctorAndDate(
            @HeaderParam("Authorization") String authorization,
            @PathParam("doctorUserId") Long doctorUserId,
            @QueryParam("date") String date
    ) {
        if (authorization == null || authorization.isBlank()) {
            return Response.status(401).entity(Map.of("error", "Missing Authorization header")).build();
        }
        if (doctorUserId == null) {
            return Response.status(400).entity(Map.of("error", "doctorUserId is required")).build();
        }
        if (date == null || date.isBlank()) {
            return Response.status(400).entity(Map.of("error", "date=YYYY-MM-DD is required")).build();
        }

        List<PatientDto> patients = patientClient.getForDoctor(doctorUserId, authorization);
        List<EncounterDto> encounters = clinicalClient.getEncountersForDoctorOnDate(doctorUserId, date, authorization);

        Map<Long, List<EncounterDto>> encountersByPatient = encounters.stream()
                .filter(e -> e.patientId != null)
                .collect(Collectors.groupingBy(e -> e.patientId));

        // Frontend-vänligt: per patient får du encounters-listan direkt
        List<Map<String, Object>> results = patients.stream()
                .map(p -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("patient", p);
                    row.put("encounters", encountersByPatient.getOrDefault(p.id, List.of()));
                    return row;
                })
                .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("doctorUserId", doctorUserId);
        payload.put("date", date);
        payload.put("patients", patients);
        payload.put("encountersByPatient", encountersByPatient);
        payload.put("results", results);

        return Response.ok(payload).build();
    }
}