package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExtendedCompModel {

    private static final Logger LOG = LoggerFactory.getLogger(ExtendedCompModel.class);

    @Self
    private Resource currentResource;

    private List<SlideItem> slides;
    private String galleryId;

    @PostConstruct
    protected void init() {
        LOG.debug("Initializing ExtendedCompModel for resource: {}", currentResource.getPath());

        slides = new ArrayList<>();
        galleryId = "media-gallery-" + UUID.nameUUIDFromBytes(currentResource.getPath().getBytes()).toString();
        Resource slidesContainer = currentResource.getChild("slides");

        if (slidesContainer == null) {
            LOG.warn("No 'slides' container found under resource: {}", currentResource.getPath());
            return;
        }

        LOG.info("Found 'slides' container at path: {}, number of children: {}", slidesContainer.getPath(),
                slidesContainer.getChildren().spliterator().getExactSizeIfKnown());

        int slideIndex = 0;
        for (Resource child : slidesContainer.getChildren()) {
            slideIndex++;
            ValueMap vm = child.getValueMap();
            String assetPath = vm.get("assetPath", String.class);

            LOG.debug("Processing slide {} at resource path: {}, assetPath: {}", slideIndex, child.getPath(), assetPath);

            if (assetPath == null || assetPath.isEmpty()) {
                LOG.warn("Slide {} at path {} has empty or missing 'assetPath' – skipping this slide", slideIndex, child.getPath());
                continue;
            }

            SlideItem slide = new SlideItem();
            slide.setAssetPath(assetPath);
            String slideType = vm.get("slideType", "image");
            slide.setSlideType(slideType);
            slide.setAltText(vm.get("altText", ""));
            String[] overlayLabels = vm.get("overlayLabels", String[].class);
            slide.setOverlayLabels(overlayLabels);

            // For video slides use the explicit thumbnailPath; fall back to assetPath for images.
            String thumbnailPath = vm.get("thumbnailPath", String.class);
            if (thumbnailPath != null && !thumbnailPath.isEmpty()) {
                slide.setThumbnailUrl(thumbnailPath);
            } else {
                slide.setThumbnailUrl(assetPath);
            }

            slide.setVideoPosterAlt(getVideoPosterAlt(slideType, slide.getAltText()));

            LOG.debug("Slide {} added – type: {}, altText: {}, overlayLabels: {}", slideIndex, slideType,
                    slide.getAltText(), overlayLabels != null ? String.join(",", overlayLabels) : "none");

            slides.add(slide);
        }

        LOG.info("Total slides loaded: {}", slides.size());
    for (int i = 0; i < slides.size(); i++) {
        SlideItem item = slides.get(i);
        LOG.info("Slide {} -> assetPath: {}, slideType: {}, altText: {}, overlayLabels: {}",
            i + 1,
            item.getAssetPath(),
            item.getSlideType(),
            item.getAltText(),
            item.getOverlayLabels() != null ? Arrays.toString(item.getOverlayLabels()) : "[]");
    }
    LOG.info("Total slides: {}", slides);
    }

    public List<SlideItem> getSlides() {
        return slides != null ? slides : Collections.emptyList();
    }

    public boolean hasSlides() {
        boolean has = slides != null && !slides.isEmpty();
        LOG.debug("hasSlides() = {}", has);
        return has;
    }

    public String getGalleryId() {
        return galleryId;
    }

    private String getVideoPosterAlt(String slideType, String altText) {
        if (!"video".equalsIgnoreCase(slideType)) {
            return altText;
        }

        if (altText == null || altText.isEmpty()) {
            return "Video thumbnail";
        }

        return altText;
    }

    public static class SlideItem {
        private String assetPath;
        private String slideType;
        private String altText;
        private String[] overlayLabels;
        private String thumbnailUrl;
        private String posterUrl;
        private String videoPosterAlt;

        public String getAssetPath() { return assetPath; }
        public void setAssetPath(String p) { assetPath = p; }

        public String getSlideType() { return slideType; }
        public void setSlideType(String t) { slideType = t; }

        public String getAltText() { return altText != null ? altText : ""; }
        public void setAltText(String a) { altText = a; }

        public String[] getOverlayLabels() {
            return overlayLabels;
        }
        public void setOverlayLabels(String[] l) { overlayLabels = l; }

        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String u) { thumbnailUrl = u; }

        public String getPosterUrl() { return posterUrl; }
        public void setPosterUrl(String p) { posterUrl = p; }

        public String getVideoPosterAlt() { return videoPosterAlt; }
        public void setVideoPosterAlt(String a) { videoPosterAlt = a; }

        public boolean isVideo() {
            return "video".equalsIgnoreCase(slideType);
        }

        public boolean isImage() {
            return !isVideo();
        }

        @Override
        public String toString() {
            return "SlideItem{" +
                    "assetPath='" + assetPath + '\'' +
                    ", slideType='" + slideType + '\'' +
                    ", altText='" + getAltText() + '\'' +
                    ", overlayLabels=" + Arrays.toString(overlayLabels) +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    ", posterUrl='" + posterUrl + '\'' +
                    ", videoPosterAlt='" + videoPosterAlt + '\'' +
                    '}';
        }
    }
}