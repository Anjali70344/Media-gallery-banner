package com.adobe.aem.guides.wknd.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.aem.guides.wknd.core.testcontext.AppAemContext;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class MediaGalleryBannerModelTest {

    private final AemContext context = AppAemContext.newAemContext();

    private Resource bannerResource;

    @BeforeEach
    void setUp() {
        Page page = context.create().page("/content/test-page");
        bannerResource = context.create().resource(page, "mediaGalleryBanner",
                "sling:resourceType", "wknd/components/mediaGalleryBanner");
    }

    @Test
    void testSlidesAreLoadedForImageAndVideo() {
        context.create().resource(bannerResource, "slides/item0",
                "assetPath", "/content/dam/wknd/banner-1.jpg",
                "slideType", "image",
                "altText", "Hero image",
                "overlayLabels", new String[] { "Flash Sale", "Limited-Time Savings" });

        context.create().resource(bannerResource, "slides/item1",
                "assetPath", "/content/dam/wknd/promo-video.mp4",
                "slideType", "video",
            "thumbnailPath", "/content/dam/wknd/promo-video-poster.jpg",
                "altText", "Promo video",
                "overlayLabels", new String[] { "New" });

        MediaGalleryBannerModel model = bannerResource.adaptTo(MediaGalleryBannerModel.class);

        assertNotNull(model);
        assertTrue(model.hasSlides());

        List<MediaGalleryBannerModel.SlideItem> slides = model.getSlides();
        assertEquals(2, slides.size());

        MediaGalleryBannerModel.SlideItem imageSlide = slides.get(0);
        assertEquals("/content/dam/wknd/banner-1.jpg", imageSlide.getAssetPath());
        assertEquals("Hero image", imageSlide.getAltText());
        assertTrue(imageSlide.isImage());
        assertFalse(imageSlide.isVideo());
        assertEquals(2, imageSlide.getOverlayLabels().length);

        MediaGalleryBannerModel.SlideItem videoSlide = slides.get(1);
        assertEquals("/content/dam/wknd/promo-video.mp4", videoSlide.getAssetPath());
        assertEquals("/content/dam/wknd/promo-video-poster.jpg", videoSlide.getThumbnailUrl());
        assertEquals("Promo video", videoSlide.getAltText());
        assertTrue(videoSlide.isVideo());
        assertFalse(videoSlide.isImage());
    }

    @Test
    void testDefaultsAndFiltering() {
        context.create().resource(bannerResource, "slides/item0",
                "assetPath", "/content/dam/wknd/default-slide.jpg");

        context.create().resource(bannerResource, "slides/item1",
                "slideType", "image",
                "altText", "No asset path");

        MediaGalleryBannerModel model = bannerResource.adaptTo(MediaGalleryBannerModel.class);

        assertNotNull(model);
        assertTrue(model.hasSlides());
        assertEquals(1, model.getSlides().size());

        MediaGalleryBannerModel.SlideItem slide = model.getSlides().get(0);
        assertEquals("image", slide.getSlideType());
        assertEquals("", slide.getAltText());
        assertTrue(slide.isImage());
    }

    @Test
    void testNoSlidesContainer() {
        MediaGalleryBannerModel model = bannerResource.adaptTo(MediaGalleryBannerModel.class);

        assertNotNull(model);
        assertFalse(model.hasSlides());
        assertTrue(model.getSlides().isEmpty());
    }
}
