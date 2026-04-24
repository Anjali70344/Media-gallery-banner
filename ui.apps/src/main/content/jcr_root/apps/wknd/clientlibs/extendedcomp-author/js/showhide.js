(function (document, $) {
    "use strict";

    // Initialize when dialog opens or a new multifield row is added
    $(document).on("foundation-contentloaded", function (e) {
        $(".cq-dialog-dropdown-showhide", e.target).each(function () {
            initShowHide(this);
        });
    });

    function initShowHide(triggerEl) {
        // Guard: prevent attaching duplicate change listeners on the same element
        if ($(triggerEl).data("showhide-initialized")) return;
        $(triggerEl).data("showhide-initialized", true);

        // granite:class is on the wrapper div; find the coral-select inside it
        var coralSelect = $(triggerEl).is("coral-select")
            ? triggerEl
            : $(triggerEl).find("coral-select")[0];
        if (!coralSelect) return;

        Coral.commons.ready(coralSelect, function (component) {
            // Apply on initial load (handles pre-populated saved values)
            applyShowHide(triggerEl, component);
            // Apply on every user change
            component.on("change", function () {
                applyShowHide(triggerEl, component);
            });
        });
    }

    /**
     * Walk UP from the trigger element to find the NEAREST ancestor
     * that contains the target selector. This naturally scopes to the
     * current composite multifield row without depending on Coral element names.
     */
    function getScopedTargets(triggerEl, target) {
        var $el = $(triggerEl).parent();
        for (var i = 0; i < 15; i++) {
            if (!$el.length || $el.is("body")) break;
            var $found = $el.find(target);
            if ($found.length) return $found;
            $el = $el.parent();
        }
        return $();
    }

    function applyShowHide(triggerEl, component) {
        var target = $(triggerEl).data("cqDialogDropdownShowhideTarget");
        if (!target) return;

        // Scope targets to THIS row only — never fall back to document-wide search
        var $targets = getScopedTargets(triggerEl, target);
        if (!$targets.length) return;

        // Hide all targets in this row first
        $targets.addClass("hide");

        // Safely read selected value(s) — component.values is a Coral collection, not a plain Array
        var selectedValues = [];
        if (component.values && component.values.length) {
            for (var i = 0; i < component.values.length; i++) {
                selectedValues.push(component.values[i]);
            }
        } else if (component.value) {
            selectedValues = [component.value];
        }

        // Show only the targets whose showhidetargetvalue matches the current selection
        $targets.each(function () {
            var targetValue = $(this).data("showhidetargetvalue");
            if (typeof targetValue !== "undefined") {
                var vals = String(targetValue).replace(/ /g, "").split(",");
                for (var j = 0; j < vals.length; j++) {
                    if (selectedValues.indexOf(vals[j]) !== -1) {
                        $(this).removeClass("hide");
                        break;
                    }
                }
            }
        });
    }
})(document, Granite.$);