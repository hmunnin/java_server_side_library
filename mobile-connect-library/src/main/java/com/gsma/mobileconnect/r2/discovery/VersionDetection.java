package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.exceptions.InvalidScopeException;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VersionDetection {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionDetection.class);

    public static String getCurrentVersion(String version, String scope, ProviderMetadata providerMetadata) throws InvalidScopeException {
        List<String> supportedVersions = getSupportedVersions(providerMetadata);
        if (version != null && isVersionSupported(version)) {
            if (!supportedVersions.contains(version)) {
                LOGGER.warn(String.format("Check version. It might be unsupported (\"%s\")", version));
            }
            return version;
        } else {
            List<String> currentScopes = StringUtils.convertToListBySpase(scope);
            if (supportedVersions.contains(Version.MC_DI_R2_V2_3) & containsScopesV2_3(currentScopes)) {
                return Version.MC_DI_R2_V2_3;
            } else if (supportedVersions.contains(Version.MC_V2_0) & containsScopesV2_0(currentScopes)) {
                return Version.MC_V2_0;
            } else if (supportedVersions.contains(Version.MC_V1_1) & containsScopesV1_1(currentScopes)) {
                return Version.MC_V1_1;
            } else if(supportedVersions.contains(Version.MC_V1_2) & supportedVersions.size() == 1 & containsScopesV2_0(currentScopes)) {
                LOGGER.warn("Version is deprecated");
                return Version.MC_V1_2;
            } else {
                final InvalidScopeException scopeException = new InvalidScopeException(scope);
                throw scopeException;
            }
        }
    }

    private static boolean isVersionSupported(String version) {
        return version.equals(Version.MC_V1_1) || version.equals(Version.MC_V1_2) || version.equals(Version.MC_V2_0)
                || version.equals(Version.MC_DI_R2_V2_3);
    }

    private static boolean containsOpenidScope(List<String> currentScopes) {
        return currentScopes.contains(Scope.OPENID);
    }

    private static boolean containsUniversalScopes(List<String> currentScopes) {
        return currentScopes.contains(Scope.MC_INDIA_TC) || currentScopes.contains(Scope.MC_MNV_VALIDATE)
                || currentScopes.contains(Scope.MC_MNV_VALIDATE_PLUS) || currentScopes.contains(Scope.MC_ATTR_VM_SHARE)
                || currentScopes.contains(Scope.MC_VM_SHARE)
                || currentScopes.contains(Scope.MC_ATTR_VM_SHARE_HASH)
                || currentScopes.contains(Scope.VM_MATCH)
                || currentScopes.contains(Scope.VM_MATCH_HASH)
                || currentScopes.contains(Scope.ATTR_VM_MATCH) || currentScopes.contains(Scope.ATTR_VM_MATCH_HASH);
    }

    private static boolean containsScopesV1_1(List<String> currentScopes) {
        return (containsOpenidScope(currentScopes) & currentScopes.size() == 1) ||
                (containsOpenidScope(currentScopes) & containsUniversalScopes(currentScopes));
    }

    private static boolean containsScopesV2_0(List<String> currentScopes) {
        return containsOpenidScope(currentScopes) & (currentScopes.contains(Scope.AUTHN) || currentScopes.contains(Scope.AUTHZ) ||
                currentScopes.contains(Scope.IDENTITY_PHONENUMBER) || currentScopes.contains(Scope.PHONENUMBER) ||
                currentScopes.contains(Scope.IDENTITY_NATIONALID) || currentScopes.contains(Scope.NATIONALID) ||
                currentScopes.contains(Scope.IDENTITY_SIGNUP) || currentScopes.contains(Scope.SIGNUP) ||
                currentScopes.contains(Scope.IDENTITY_SIGNUPPLUS) || containsUniversalScopes(currentScopes));
    }

    private static boolean containsScopesV2_3(List<String> currentScopes) {
        return containsOpenidScope(currentScopes) & (containsScopesV2_0(currentScopes) || currentScopes.contains(Scope.KYC_HASHED)
                || currentScopes.contains(Scope.KYC_PLAIN));
    }

    private static List<String> getSupportedVersions(ProviderMetadata providerMetadata) {
        List<String> supportedVersions = new ArrayList<>();
        if (providerMetadata == null || providerMetadata.getMCVersion() == null) {
            supportedVersions.add(Version.MC_V1_1);
        } else {
            supportedVersions = providerMetadata.getMCVersion();
        }
        return supportedVersions;
    }
}
