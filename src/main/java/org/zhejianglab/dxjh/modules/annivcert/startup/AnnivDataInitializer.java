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
        for (int i = 1; i <= 12; i++) {
            String code = String.format("SCS%02d", i);
            if (!quotaRepo.existsById(code)) {
                AnnivScsQuota q = new AnnivScsQuota();
                q.setScsCode(code);
                q.setIssued(0);
                q.setLimitCnt(125);
                quotaRepo.save(q);
            }
        }
        if (!seqRepo.existsById("ANNIV_CERT")) {
            AnnivSeqCounter sc = new AnnivSeqCounter();
            sc.setName("ANNIV_CERT");
            sc.setLastSeq(0); // 若要预留 0001，改为 1 并预插那条证书记录
            seqRepo.save(sc);
        }
    }

}
