/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.aihdreports.fragment.controller;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.aihdreports.reporting.calculation.Calculations;
import org.openmrs.module.aihdreports.reporting.calculation.EmrCalculationUtils;
import org.openmrs.module.aihdreports.reporting.library.cohort.CommonCohortLibrary;
import org.openmrs.module.aihdreports.reporting.metadata.Dictionary;
import org.openmrs.module.aihdreports.reporting.utils.Filters;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DiabeticHypertensionFragmentController {

    public void controller(FragmentModel model) {

        //set context
        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);
        PatientCalculationContext context = patientCalculationService.createCalculationContext();
        context.setNow(new Date());
        //get a collection of all patients
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        List<Integer> cohort = new ArrayList<>();
        //loop through all and get their patient ids
        if(allPatients.size() > 0) {
            for (Patient patient : allPatients) {
                cohort.add(patient.getPatientId());
            }
        }
        //exclude dead patients
        Set<Integer> alivePatients = Filters.alive(cohort, context);
        Set<Integer> male = Filters.male(alivePatients, context);
        Set<Integer> female = Filters.female(alivePatients, context);
        //declare concepts
        Concept diabetic = Dictionary.getConcept(Dictionary.DIABETIC_VISIT_TYPE);
        Concept newDiabetic = Dictionary.getConcept(Dictionary.NEW_DIABETIC_PATIENT);
        Concept knownDiabetic = Dictionary.getConcept(Dictionary.KNOWN_DAIBETIC_PATIENT);
        Concept hypertension = Dictionary.getConcept(Dictionary.HYPERTENSION_VISIT_TYPE);
        Concept newHypertension = Dictionary.getConcept(Dictionary.NEW_HYPERTENSION_PATIENT);
        Concept knownHypertension = Dictionary.getConcept(Dictionary.KNOWN_HYPERTENSION_PATIENT);
        //start formulating the values to be displayed on the viewer for diabetes
        model.addAttribute("diabeticMaleZeroTo5", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, male, context, 0, 5));
        model.addAttribute("diabeticMale6To18", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, male, context, 6, 18));
        model.addAttribute("diabeticMale19To35", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, male, context, 19, 35));
        model.addAttribute("diabeticMale36To60", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, male, context, 36, 60));
        model.addAttribute("diabeticMale60To120", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, male, context, 60, 120));
        model.addAttribute("diabeticMaleTotals", getDiabeticTotalPatients(diabetic, newDiabetic, knownDiabetic, male, context));

        model.addAttribute("diabeticFemaleZeroTo5", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, female, context, 0, 5));
        model.addAttribute("diabeticFemale6To18", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, female, context, 6, 18));
        model.addAttribute("diabeticFemale19To35", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, female, context, 19, 35));
        model.addAttribute("diabeticFemale36To60", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, female, context, 36, 60));
        model.addAttribute("diabeticFemale60To120", getDiabeticPatients(diabetic, newDiabetic, knownDiabetic, female, context, 60, 120));
        model.addAttribute("diabeticFemaleTotals", getDiabeticTotalPatients(diabetic, newDiabetic, knownDiabetic, female, context));

        //hypertension
        model.addAttribute("hypertensionMaleZeroTo5", getDiabeticPatients(hypertension, newHypertension, knownHypertension, male, context, 0, 5));
        model.addAttribute("hypertensionMale6To18", getDiabeticPatients(hypertension, newHypertension, knownHypertension, male, context, 6, 18));
        model.addAttribute("hypertensionMale19To35", getDiabeticPatients(hypertension, newHypertension, knownHypertension, male, context, 19, 35));
        model.addAttribute("hypertensionMale36To60", getDiabeticPatients(hypertension, newHypertension, knownHypertension, male, context, 36, 60));
        model.addAttribute("hypertensionMale60To120", getDiabeticPatients(hypertension, newHypertension, knownHypertension, male, context, 60, 120));
        model.addAttribute("hypertensionMaleTotals", getDiabeticTotalPatients(hypertension, newHypertension, knownHypertension, male, context));

        model.addAttribute("hypertensionFemaleZeroTo5", getDiabeticPatients(hypertension, newHypertension, knownHypertension, female, context, 0, 5));
        model.addAttribute("hypertensionFemale6To18", getDiabeticPatients(hypertension, newHypertension, knownHypertension, female, context, 6, 18));
        model.addAttribute("hypertensionFemale19To35", getDiabeticPatients(hypertension, newHypertension, knownHypertension, female, context, 19, 35));
        model.addAttribute("hypertensionFemale36To60", getDiabeticPatients(hypertension, newHypertension, knownHypertension, female, context, 36, 60));
        model.addAttribute("hypertensionFemale60To120", getDiabeticPatients(hypertension, newHypertension, knownHypertension, female, context, 60, 120));
        model.addAttribute("hypertensionFemaleTotals", getDiabeticTotalPatients(hypertension, newHypertension, knownHypertension, female, context));

    }

    private Integer getDiabeticPatients(Concept q, Concept a1, Concept a2, Set<Integer> cohort, PatientCalculationContext context, int minAge, int maxAge){
        Set<Integer> allSet = new HashSet<>();
        CalculationResultMap diabeticMap = Calculations.lastObs(q, cohort, context);

        for(Integer pId: cohort){
            Obs obs = EmrCalculationUtils.obsResultForPatient(diabeticMap, pId);
            if(obs != null && (obs.getValueCoded().equals(a1) || obs.getValueCoded().equals(a2))){
                if(obs.getPerson().getAge() >= minAge && obs.getPerson().getAge() <= maxAge) {
                    allSet.add(pId);
                }
            }

        }
        return allSet.size();
    }
    private Integer getDiabeticTotalPatients(Concept q, Concept a1, Concept a2, Set<Integer> cohort, PatientCalculationContext context){
        Set<Integer> allSet = new HashSet<>();
        CalculationResultMap diabeticMap = Calculations.lastObs(q, cohort, context);

        for(Integer pId: cohort){
            Obs obs = EmrCalculationUtils.obsResultForPatient(diabeticMap, pId);
            if(obs != null && (obs.getValueCoded().equals(a1) || obs.getValueCoded().equals(a2))){
                allSet.add(pId);
            }
        }
        return allSet.size();
    }


}
