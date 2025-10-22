package org.zhejianglab.dxjh.modules.annivcert.startup;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zhejianglab.dxjh.modules.annivcert.entity.*;
import org.zhejianglab.dxjh.modules.annivcert.repository.*;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Component
public class AnnivDataInitializer {
    final AnnivScsQuotaRepository quotaRepo;
    final AnnivSeqCounterRepository seqRepo;

    public AnnivDataInitializer(AnnivScsQuotaRepository q, AnnivSeqCounterRepository s) {
        this.quotaRepo = q; this.seqRepo = s;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        final String V1 = "1";                 // 历史 12 颗星 → 版本 1
        final String V2 = "2";                 // 新增 12 颗星 → 版本 2
        final int LIMIT_PER_SCS = 125;

        // 0) 给历史无版本的数据打上 version=1（只改 version_code IS NULL 的记录）
        quotaRepo.patchNullVersionTo(V1);

        // 1) 版本 2 的 12 颗星（SCS01..SCS12）若不存在则补齐
        for (int i = 1; i <= 12; i++) {
            String scs = String.format("SCS%02d", i);
            if (!quotaRepo.existsByVersionCodeAndScsCode(V2, scs)) {
                AnnivScsQuota q = new AnnivScsQuota();
                q.setVersionCode(V2);
                q.setScsCode(scs);
                q.setLimitCnt(LIMIT_PER_SCS);
                q.setIssued(0);
                quotaRepo.save(q);
            }
        }

        // 2) 序列计数器：保留老的 ANNIV_CERT，同时新增开放日专用的 ANNIV_CERT_OPEN_DAY
        ensureSeqExists("ANNIV_CERT");              // 周年版，若已存在不触碰 lastSeq
        ensureSeqExists("ANNIV_OPEN_DAY");          // 开放日版，新建用 0 起步
    }

    private void ensureSeqExists(String key) {
        if (!seqRepo.existsById(key)) {
            AnnivSeqCounter sc = new AnnivSeqCounter();
            sc.setName(key);      // 主键就是 name
            sc.setLastSeq(0);
            seqRepo.save(sc);
        }
    }

}
