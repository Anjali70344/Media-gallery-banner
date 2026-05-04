package com.adobe.aem.guides.wknd.core.models;

import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.models.ListItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import java.util.List;

@Model(
    adaptables = {org.apache.sling.api.resource.Resource.class, SlingHttpServletRequest.class},
    adapters = Teaser.class,
    resourceType = "wknd/components/customteaser",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CustomTeaser implements Teaser {

    private static final String CTA_STYLE_PRIMARY = "primary";
    private static final String THEME_LIGHT = "light";
    private static final String DEFAULT_CTA_LABEL = "Learn More";

    @ValueMapValue
    private String ctaStyle;

    @ValueMapValue(name = "title")
    private String authoredTitle;

    @ValueMapValue
    private String badge;

    @ValueMapValue
    private Boolean overlayEnabled;

    @ValueMapValue
    private Boolean disableOverlay;

    @ValueMapValue
    private String customCssClass;

    @ValueMapValue
    private String theme;

    @SlingObject
    private Resource currentResource;

    @Self
    @Via(type = ResourceSuperType.class)
    private Teaser delegate;

    public String getCtaStyle() {
        return StringUtils.defaultIfBlank(ctaStyle, CTA_STYLE_PRIMARY);
    }

    public String getBadge() {
        return StringUtils.defaultString(badge);
    }

    public boolean isOverlayEnabled() {
        if (overlayEnabled != null) {
            return overlayEnabled;
        }

        return !Boolean.TRUE.equals(disableOverlay);
    }

    public String getCustomCssClass() {
        return StringUtils.defaultString(customCssClass);
    }

    public String getTheme() {
        return StringUtils.defaultIfBlank(theme, THEME_LIGHT);
    }

    public String getBackgroundImage() {
        String imagePath = extractImageFromDelegate();
        if (StringUtils.isNotBlank(imagePath)) {
            return imagePath;
        }

        if (currentResource == null) {
            return "";
        }

        ValueMap vm = currentResource.getValueMap();
        imagePath = vm.get("fileReference", String.class);
        if (StringUtils.isNotBlank(imagePath)) {
            return imagePath;
        }

        Resource imageChild = currentResource.getChild("image");
        if (imageChild != null) {
            imagePath = imageChild.getValueMap().get("fileReference", String.class);
            if (StringUtils.isNotBlank(imagePath)) {
                return imagePath;
            }
        }

        return "";
    }

    @Override
    public String getTitle() {
        if (StringUtils.isNotBlank(authoredTitle)) {
            return authoredTitle;
        }

        return delegate != null ? StringUtils.defaultString(delegate.getTitle()) : "";
    }

    @Override
    public String getDescription() {
        return delegate != null ? delegate.getDescription() : "";
    }

    public String getLinkURL() {
        return delegate != null ? StringUtils.defaultString(delegate.getLinkURL()) : "";
    }

    public String getLinkText() {
        if (StringUtils.isNotBlank(authoredTitle)) {
            return DEFAULT_CTA_LABEL;
        }

        if (delegate == null) {
            return DEFAULT_CTA_LABEL;
        }

        List<ListItem> actions = delegate.getActions();
        if (actions != null && !actions.isEmpty()) {
            String actionTitle = actions.get(0).getTitle();
            if (StringUtils.isNotBlank(actionTitle)) {
                return actionTitle;
            }
        }

        return DEFAULT_CTA_LABEL;
    }

    private String extractImageFromDelegate() {
        if (delegate == null) {
            return "";
        }

        Resource imageResource = delegate.getImageResource();
        if (imageResource == null) {
            return "";
        }

        ValueMap imageProps = imageResource.getValueMap();
        String fileReference = imageProps.get("fileReference", String.class);
        if (StringUtils.isNotBlank(fileReference)) {
            return fileReference;
        }

        return imageProps.get("src", "");
    }
}