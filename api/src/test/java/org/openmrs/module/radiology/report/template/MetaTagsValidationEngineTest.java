/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link MetaTagsValidationEngine}.
 */
public class MetaTagsValidationEngineTest {
    
    
    Attributes charsetAttributes;
    
    Attributes dublinElementAttributes;
    
    Element charsetElement;
    
    Element dublinElement;
    
    @Before
    public void setUp() {
        charsetAttributes = new Attributes();
        charsetAttributes.put("charset", "UTF-8");
        charsetElement = new Element(Tag.valueOf("meta"), "", charsetAttributes);
        
        dublinElementAttributes = new Attributes();
        dublinElementAttributes.put("name", "dcterms.title");
        dublinElementAttributes.put("content", "CT Abdomen");
        dublinElement = new Element(Tag.valueOf("meta"), "", dublinElementAttributes);
    }
    
    @Test
    public void shouldReturnValidationResultWithNoErrorsIfSubjectPassesAllChecks() throws Exception {
        
        Elements elements = new Elements(charsetElement, dublinElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertFalse(validationResult.hasErrors());
    }
    
    @Test
    public void shouldReturnValidationResultWithErrorForMetaElementCharsetAttributeIfNotPresentInSubject() throws Exception {
        
        Elements elements = new Elements(dublinElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertTrue(validationResult.hasErrors());
        assertThat(validationResult.getErrors()
                .get(0)
                .getMessageCode(),
            is("radiology.MrrtReportTemplate.validation.error.meta.charset.occurence"));
    }
    
    @Test
    public void shouldReturnValidationResultWithErrorForMetaElementCharsetAttributeIfPresentMoreThanOnceInSubject()
            throws Exception {
        
        Element otherCharsetElement = new Element(Tag.valueOf("meta"), "", charsetAttributes);
        Elements elements = new Elements(charsetElement, otherCharsetElement, dublinElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertTrue(validationResult.hasErrors());
        assertThat(validationResult.getErrors()
                .get(0)
                .getMessageCode(),
            is("radiology.MrrtReportTemplate.validation.error.meta.charset.occurence"));
    }
    
    @Test
    public void
            run_shouldReturnValidationResultWithErrorForMetaElementDublinCoreIfNoMetaElementWithNameAttributeIsPresentInSubject()
                    throws Exception {
        
        Elements elements = new Elements(charsetElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertTrue(validationResult.hasErrors());
        assertThat(validationResult.getErrors()
                .get(0)
                .getMessageCode(),
            is("radiology.MrrtReportTemplate.validation.error.meta.dublinCore.missing"));
    }
}
