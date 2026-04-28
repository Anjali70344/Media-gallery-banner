(function ($, $document) {
    var MEDIA_TYPE_SELECTOR = "[name='./slideType']";

    $document.on("foundation-contentloaded", function (e) {
        initializeFacetToggles($(e.target));
    });

    $document.off("change.extendedCompMediaToggle", MEDIA_TYPE_SELECTOR)
        .on("change.extendedCompMediaToggle", MEDIA_TYPE_SELECTOR, function () {
            toggleMediaFields($(this));
        });

    $document.off("coral-collection:add.extendedCompMediaToggle", "coral-multifield")
        .on("coral-collection:add.extendedCompMediaToggle", "coral-multifield", function (event) {
            initializeFacetToggles($(event.detail.item));
        });

    function initializeFacetToggles($container) {
        $container.find(MEDIA_TYPE_SELECTOR).each(function () {
            toggleMediaFields($(this));
        });
    }

    function toggleMediaFields($toggle) {
        var $multifieldItem = $toggle.closest("coral-multifield-item");
        var $imageContainer = $multifieldItem.find(".image-container");
        var $videoContainer = $multifieldItem.find(".video-container");

        if ($toggle.val() === "video") {
            $imageContainer.hide();
            $videoContainer.show();
        } else {
            $imageContainer.show();
            $videoContainer.hide();
        }
    }

})(Granite.$, jQuery(document));