function validateForm() {
        let startDate = jQuery('#openmrs-start-date').datepicker('getDate');
        let endDate = jQuery('#openmrs-end-date').datepicker('getDate');
        if(startDate == '') {
        jQuery('#openmrs-start-date').parent().append('<span class="error" id="startDateEmptyError">Start date cannot be empty.</span>');
        event.preventDefault();
        return;
        }else if(endDate == ''){
        jQuery('#openmrs-end-date').parent().append('<span class="error" id="endDateEmptyError">End date cannot be empty.</span>');
        event.preventDefault();
        return;
        }
    }